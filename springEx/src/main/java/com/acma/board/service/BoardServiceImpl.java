

package com.acma.board.service;


import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.acma.board.domain.Board;
import com.acma.board.repository.BoardRepository;
import com.co.kr.code.Code;
import com.co.kr.domain.BoardContentDomain;
import com.co.kr.domain.BoardFileDomain;
import com.co.kr.domain.BoardListDomain;
import com.co.kr.exception.RequestException;
import com.co.kr.mapper.UploadMapper;
import com.co.kr.util.CommonUtils;
import com.co.kr.vo.FileListVO;

import lombok.extern.slf4j.Slf4j;

@Service
public class BoardServiceImpl implements BoardService {
 
    @Autowired
    private BoardRepository boardRepository;
    
    @Override
    public Page<Board> findBoardList(Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1,
            pageable.getPageSize());
        return boardRepository.findAll(pageable);
    }
 
    @Override
    public Board findBoardByIdx(Long idx) {
        return boardRepository.findById(idx).orElse(new Board());
    }
}