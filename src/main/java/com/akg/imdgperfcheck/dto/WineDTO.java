package com.akg.imdgperfcheck.dto;

import lombok.Data;

@Data
public class WineDTO {

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
