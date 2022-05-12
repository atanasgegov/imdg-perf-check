package com.akg.imdgperfcheck.service;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akg.imdgperfcheck.config.pojo.Query;
import com.akg.imdgperfcheck.config.pojo.QueryParams;
import com.akg.imdgperfcheck.dto.WineDTO;
import com.akg.imdgperfcheck.util.QueryUtil;
import com.hazelcast.aggregation.Aggregators;
import com.hazelcast.config.IndexConfig;
import com.hazelcast.config.IndexType;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicates;

import lombok.extern.slf4j.Slf4j;

@Service("hazelcastCommand")
@Slf4j
public class HazelcastCommand extends AbstractCommand {

	@Autowired(required=false)
	private HazelcastInstance hazelcastInstance;

	private IMap<Long, WineDTO> wineMap;

	@PostConstruct
	public void init() {
		if( hazelcastInstance != null ) {
			wineMap = hazelcastInstance.getMap( "wine" );
			wineMap.addIndex(new IndexConfig(IndexType.HASH, "country"));
			wineMap.addIndex(new IndexConfig(IndexType.HASH, "points"));
			wineMap.addIndex(new IndexConfig(IndexType.HASH, "price"));
		}
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
		String actualQueryString = QueryUtil.setParamsToQuery( query.getExec(), query.getParams() );
		wineMap.values( Predicates.sql( actualQueryString ) );

		return 1;
	}

	@Override
	protected Integer updateRequest(Query query) {
		String actualQueryString = QueryUtil.setParamsToQuery( query.getExec(), query.getParams() );
		Collection<WineDTO> wines = wineMap.values( Predicates.sql( actualQueryString ) );
		int updateCounter = 0;
		for (WineDTO wineDTO : wines) {
			QueryParams qp = QueryUtil.getRandomChoosenQueryParams(query.getParams());
			wineDTO = this.updateWineDTO(wineDTO, query.getAdditionalExec(), qp.getParam2());
			wineMap.put( wineDTO.getId(), wineDTO );
			updateCounter++;
		}

		return updateCounter;
	}
	
	@Override
	protected Integer deleteRequest(Query query) {
		String actualQueryString = QueryUtil.setParamsToQuery( query.getExec(), query.getParams() );
		Collection<WineDTO> wines = wineMap.values( Predicates.sql( actualQueryString ) );
		int deleteCounter = 0;
		for (WineDTO wineDTO : wines) {
			wineMap.remove(wineDTO.getId());
			deleteCounter++;
		}
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
		if( hazelcastInstance != null ) {
			hazelcastInstance.shutdown();
		}
    }


	/*
	 * Stupid method but needed, Hazelcast has no functionality that can update in dynamic way multi-field value or in this case to update a field in Object  
	 */
	private WineDTO updateWineDTO(WineDTO wine, String fieldName, String value) {
		switch (fieldName) {
			case "country": wine.setCountry(String.valueOf(value)); break;
			case "points": wine.setPoints(Integer.valueOf(value)); break;
			case "price": wine.setPrice(Float.valueOf(value)); break;
			default:
				break;
		}
		
		return wine;
	}
}
