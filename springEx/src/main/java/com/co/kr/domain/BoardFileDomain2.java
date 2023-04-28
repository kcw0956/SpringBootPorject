package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName="builder")
public class BoardFileDomain2 {

	
	private Integer bdSeq2;
	private String mbId2;
	
	private String upOriginalFileName2;
	private String upNewFileName2; //동일 이름 업로드 될 경우
	private String upFilePath2;
	private Integer upFileSize2;
	
}