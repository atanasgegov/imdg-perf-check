package com.akg.imdgperfcheck.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akg.imdgperfcheck.config.pojo.Query;
import com.akg.imdgperfcheck.config.pojo.QueryParams;
import com.akg.imdgperfcheck.dto.WineDTO;
import com.akg.imdgperfcheck.util.QueryUtil;

import io.redisearch.AggregationResult;
import io.redisearch.Document;
import io.redisearch.SearchResult;
import io.redisearch.aggregation.AggregationBuilder;
import io.redisearch.aggregation.reducers.Reducers;
import io.redisearch.client.Client;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@Service("redisCommand")
@Slf4j
public class RedisCommand extends AbstractCommand {

	@Autowired(required=false)
	private Client client;
	
	@Override
	protected Integer insertRequest(List<WineDTO> data) {

		Map<String, String> fields = new HashMap<>();
		try(Jedis conn = client.connection()){
			for (WineDTO wine : data) {

				String id = String.valueOf(wine.getId());
				fields.put("id", id);
				fields.put("num", String.valueOf(wine.getNum()));
				fields.put("country", wine.getCountry());
				fields.put("description", wine.getDescription());
				fields.put("designation", wine.getDesignation());
				fields.put("points", String.valueOf(wine.getPoints()));
				fields.put("price", String.valueOf(wine.getPrice()));
				fields.put("province", wine.getProvince());
				fields.put("region1", wine.getRegion1());
				fields.put("region2", wine.getRegion2());
				fields.put("variety", wine.getVariety());
				fields.put("winery", wine.getWinery());
				conn.hset("wine:"+id, fields);
			}
		}

		return data.size();
	}

	@Override
	protected Integer searchRequest(Query query) {
		SearchResult searchResult = this.getSearchResult(query);
		Math.toIntExact(searchResult.totalResults);

		return 1;
	}

	@Override
	protected Integer updateRequest(Query query) {
		SearchResult searchResult = this.getSearchResult(query);
		int updateCounter = 0;
		try(Jedis conn = client.connection()){
			for (Document doc : searchResult.docs) {
				QueryParams qp = QueryUtil.getRandomChoosenQueryParams(query.getParams());
				conn.hset(doc.getId(), query.getAdditionalExec(), qp.getParam2());
				updateCounter++;
			}
		}
		return updateCounter;
	}

	@Override
	protected Integer deleteRequest(Query query) {
		SearchResult searchResult = this.getSearchResult(query);
		int deleteCounter = 0;
		try(Jedis conn = client.connection()){
			for (Document doc : searchResult.docs) {
				conn.del(doc.getId());
				deleteCounter++;
			}
		}
		return deleteCounter;
	}

	@Override
	protected Long getMaxId() {
		String maxIdAliasResultColumn = "maxid";
		AggregationBuilder r = new AggregationBuilder()
				  .groupBy("@flag", Reducers.max("@id").as(maxIdAliasResultColumn));

		AggregationResult res = client.aggregate(r);
		long maxId = 0;
		try {
			maxId = res.getRow(0).getLong(maxIdAliasResultColumn);
		} catch(Exception e) {
			log.error(e.getMessage());
		}

		return maxId;
	}

	@Override
	public void closeResources() {
		client.close();
	}

	private SearchResult getSearchResult(Query query) {
		String actualQueryString = QueryUtil.setParamsToQuery( query.getExec(), query.getParams() );
		io.redisearch.Query q = new io.redisearch.Query(actualQueryString).limit(query.getOffset(), query.getLimit());

		return client.search(q);
	}
}
