package com.akg.imdgperfcheck.exec;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.akg.imdgperfcheck.config.AbstractConfig;
import com.akg.imdgperfcheck.config.CommonConfig;
import com.akg.imdgperfcheck.config.pojo.Execution;
import com.akg.imdgperfcheck.config.pojo.UseCases;
import com.akg.imdgperfcheck.service.AbstractCommand;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Executor {
	
	private static final String PREFIX_BEAN_CONFIG = "Config";
	private static final String PREFIX_BEAN_COMMAND = "Command";
	
	@Autowired
	private CommonConfig commonConfig;
	
	@Autowired 
	private BeanFactory beanFactory;
	
	@Autowired
	private ApplicationContext context;
	
	private ScheduledExecutorService shutdowner = Executors.newScheduledThreadPool(1);
	
	@EventListener(ApplicationReadyEvent.class)
	@Order(1)
	public void exec() {

	    long start = System.currentTimeMillis();
		if( commonConfig.getActiveUseCase().equals( UseCases.Type.ONE ) ) {

			this.run( commonConfig.getUseCases().getOne() );
		} else if( commonConfig.getActiveUseCase().equals( UseCases.Type.CRUD ) ) {

			for(Execution execution : commonConfig.getUseCases().getCrud()) {
				this.run(execution);
			}
		} else {
			log.warn("Please, check your configuration, probably the wrong value was set for active-use-case: {}", commonConfig.getActiveUseCase());		
		}

		long end = System.currentTimeMillis();
		log.info("Elapsed Time in seconds: "+ (double)(end-start)/1000);
		int shutdownAfter = 1;
		log.info("The application will shutdown in {} seconds.", shutdownAfter);
		this.shutdown(shutdownAfter);
	}

	private void run( Execution execution ) {

		String what = execution.getWhat();
		String mode = execution.getMode();
		AbstractConfig config = beanFactory.getBean( what+PREFIX_BEAN_CONFIG, AbstractConfig.class );
		AbstractCommand command = beanFactory.getBean(what+PREFIX_BEAN_COMMAND, AbstractCommand.class);

		log.info( "Executing '{}' '{}' queries for {} ms ...", what, mode, execution.getTimeInMs() );
		if( mode.equals( AbstractConfig.ExecutionMode.SEARCH.getValue() ) ) {
			command.search(execution, config.getSearchQueries());
		} else if( mode.equals( AbstractConfig.ExecutionMode.INSERTS.getValue() ) ) {
			command.insert(execution);
		} else if( mode.equals( AbstractConfig.ExecutionMode.UPDATES.getValue() ) ) {
			command.update(execution, config.getUpdateQueries());
		} else if( mode.equals( AbstractConfig.ExecutionMode.DELETES.getValue() ) ) {
			command.delete(execution, config.getDeleteQueries());
		} else {
			log.warn("Wrong execution.mode value: '{}' for execution.what='{}' in the configuration file.", mode, what);
		}
	}
	
	private void shutdown(int seconds) {
		Runnable shutdownerTask = () -> { 
			((ConfigurableApplicationContext) context).close();
			System.exit(0);
		};
		shutdowner.schedule(shutdownerTask, seconds, TimeUnit.SECONDS);
	}
}

