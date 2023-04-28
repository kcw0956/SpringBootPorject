package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName="builder")
public class BookListDomain {

	private String bdSeq;
	private String mbId;
	private String bdTitle;
	private String bdContent;
	private String bdCreateAt;
	private String bdUpdateAt;

}