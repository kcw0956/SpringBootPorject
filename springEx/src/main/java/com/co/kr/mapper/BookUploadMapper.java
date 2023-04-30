package com.co.kr.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;


import com.co.kr.domain.BookContentDomain;
import com.co.kr.domain.BookFileDomain;
import com.co.kr.domain.BookListDomain;


@Mapper
public interface BookUploadMapper {

	// 전체 리스트 조회
		public List<BookListDomain> bookList();
		//content insert
		public void bookcontentUpload(BookContentDomain boardContentDomain);
		//file insert
		public void bookfileUpload(BookFileDomain boardFileDomain);

		//content update
		public void bkContentUpdate(BookContentDomain boardContentDomain);
		//file updata
		public void bkFileUpdate(BookFileDomain boardFileDomain);

	  //content delete 
		public void bkContentRemove(HashMap<String, Object> map);
		//file delete 
		public void bkFileRemove(BookFileDomain boardFileDomain);
		
		//select one
		public BookListDomain bookSelectOne(HashMap<String, Object> map);

		//select one file
		public List<BookFileDomain> bookSelectOneFile(HashMap<String, Object> map);

}
