package com.acma.board.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.co.kr.domain.BoardFileDomain;
import com.co.kr.domain.BoardListDomain;
import com.co.kr.vo.FileListVO;
import com.acma.board.domain.Board;
 
public interface BoardService {
    Page<Board> findBoardList(Pageable pageable);
    Board findBoardByIdx(Long idx);
}