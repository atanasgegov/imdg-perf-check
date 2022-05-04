package com.akg.imdgperfcheck.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.akg.imdgperfcheck.dto.WineDTO;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WinesDataLoader {

	public static List<WineDTO> load(int startIndex, int endIndex, String inputDataFile, long maxId ) throws IOException {
		List<WineDTO> list = new ArrayList<>();
		try( Reader in = new FileReader(inputDataFile) ) {
			Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);

			int counter = 0;
			for (CSVRecord csvRecord : records) {
				if( counter++ < startIndex ) {
					continue;
				}
				if( counter > endIndex ) {
					break;
				}

				WineDTO wmDTO = new WineDTO();
				wmDTO.setId(++maxId);
				wmDTO.setNum(Integer.valueOf(csvRecord.get(0)));
				wmDTO.setCountry(csvRecord.get(1));
				wmDTO.setDescription(csvRecord.get(2));
				wmDTO.setDesignation(csvRecord.get(3));
				wmDTO.setPoints( StringUtil.isNullOrEmpty( csvRecord.get(4) ) ? -1 : Integer.valueOf(csvRecord.get(4)) );
				wmDTO.setPrice( StringUtil.isNullOrEmpty( csvRecord.get(5) ) ? -1 : Float.valueOf(csvRecord.get(5)) );
				wmDTO.setProvince(csvRecord.get(6));
				wmDTO.setRegion1(csvRecord.get(7));
				wmDTO.setRegion2(csvRecord.get(8));
				wmDTO.setVariety(csvRecord.get(9));
				wmDTO.setWinery(csvRecord.get(10));
				list.add(wmDTO);
			}
			log.debug( "The current CSV file offset is {}", counter );
		}
		
		return list;
	}

	public static int getNumberOfRows(String inputDataFile ) throws IOException {
		
		int counter = 0;
		try(BufferedReader br = new BufferedReader(new FileReader(inputDataFile))) {  
			while (br.readLine() != null) {
				counter++;
			}
		}

		return counter;
	}
}
