package com.akg.imdgperfcheck.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akg.imdgperfcheck.config.pojo.Query;
import com.akg.imdgperfcheck.dto.WineDTO;
import com.hazelcast.aggregation.Aggregators;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import lombok.extern.slf4j.Slf4j;

@Service("hazelcastCommand")
@Slf4j
public class HazelcastCommand extends AbstractCommand {

	@Autowired
	private HazelcastInstance hazelcastInstance;

	private IMap<Long, WineDTO> wineMap;

	@PostConstruct
	public void init() {
		wineMap = hazelcastInstance.getMap( "wine2" );
	}

	@Override
	protected Integer insertRequest(List<WineDTO> data) {

		for (WineDTO wineDTO : data) {
			wineMap.put( wineDTO.getId(), wineDTO );
		}
		return data.size();
	}

	@Override
	protected Integer searchRequest(Query query) {
		return 1;
	}

	@Override
	protected Integer updateRequest(Query query) {
		int updateCounter = 0;
		return updateCounter;
	}

	@Override
	protected Integer deleteRequest(Query query) {
		int deleteCounter = 0;
		return deleteCounter;
	}

	@Override
	protected Long getMaxId() {
		long maxId = 0;
		try {
			maxId = wineMap.aggregate(Aggregators.longMax("id"));
		} catch(Exception e) {
			log.error(e.getMessage());
		}

		return maxId;
	}

	@Override
	public void closeResources() {
		hazelcastInstance.shutdown();
    }
}
