package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName="builder")
public class BoardListDomain2 {

	private String bdSeq2;
	private String mbId2;
	private String bdTitle2;
	private String bdContent2;
	private String bdCreateAt2;
	private String bdUpdateAt2;

}