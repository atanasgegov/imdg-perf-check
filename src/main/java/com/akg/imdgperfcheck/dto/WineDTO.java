package com.akg.imdgperfcheck.dto;

import java.io.IOException;
import java.io.Serializable;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;

import lombok.Data;

@Data
public class WineDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private int num;
	private String country;
	private String description;
	private String designation;
	private int points;
	private float price;
	private String province;
	private String region1;
	private String region2;
	private String variety;
	private String winery;
}
