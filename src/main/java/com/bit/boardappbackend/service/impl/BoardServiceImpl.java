package com.bit.boardappbackend.service.impl;

import com.bit.boardappbackend.common.FileUtils;
import com.bit.boardappbackend.dto.BoardDto;
import com.bit.boardappbackend.dto.BoardFileDto;
import com.bit.boardappbackend.entity.Board;
import com.bit.boardappbackend.entity.BoardFile;
import com.bit.boardappbackend.entity.Member;
import com.bit.boardappbackend.repository.BoardFileRepository;
import com.bit.boardappbackend.repository.BoardRepository;
import com.bit.boardappbackend.service.BoardService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;
    private final FileUtils fileUtils;

    @Override
    public Page<BoardDto> post(BoardDto boardDto, MultipartFile[] uploadFiles,
                               Member member, Pageable pageable) {
        boardDto.setRegdate(LocalDateTime.now());
        boardDto.setModdate(LocalDateTime.now());

        Board board = boardDto.toEntity(member);

        if (uploadFiles != null) {
            Arrays.stream(uploadFiles).forEach(multipartFile -> {
                if(multipartFile.getOriginalFilename() != null &&
                    !multipartFile.getOriginalFilename().equalsIgnoreCase("")) {

                    BoardFileDto boardFileDto = fileUtils.parserFileInfo(multipartFile, "board/");

                    board.getBoardFileList().add(boardFileDto.toEntity(board));
                }
            });
        }

        boardRepository.save(board);

//        return boardRepository.searchAll("all", "", pageable)
//                .map(Board::toDto);
        return boardRepository.findAll(pageable).map(Board::toDto);
    }

    @Override
    public Page<BoardDto> findAll(String searchCondition, String searchKeyword, Pageable pageable) {
        return boardRepository
                .searchAll(searchCondition, searchKeyword, pageable)
                .map(Board::toDto);
    }

    @Override
    public BoardDto findById(Long id) {
        return boardRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("board not exist"))
                .toDto();
    }

    @Override
    public void deleteById(Long id) {
        boardRepository.deleteById(id);
    }

    @Override
    public BoardDto modify(BoardDto boardDto, MultipartFile[] uploadFiles, MultipartFile[] changeFiles, String originFiles, Member member) {
        List<BoardFileDto> originFileList = new ArrayList<>();

        try {
            originFileList = new ObjectMapper().readValue(
                    originFiles,
                    new TypeReference<List<BoardFileDto>>() {}
            );
        } catch(IOException ie) {
            log.error("boardService modify readvalue error: {}", ie.getMessage());
        }

        List<BoardFileDto> uFileList = new ArrayList<>();

        if(originFileList.size() > 0) {
            originFileList.forEach(boardFileDto -> {
                if(boardFileDto.getFilestatus().equalsIgnoreCase("U")
                        && changeFiles != null) {
                    Arrays.stream(changeFiles).forEach(file -> {
                        if(boardFileDto.getNewfilename().equalsIgnoreCase(file.getOriginalFilename())) {
                            BoardFileDto updateBoardFileDto = fileUtils.parserFileInfo(file, "/board");

                            updateBoardFileDto.setId(boardFileDto.getId());
                            updateBoardFileDto.setBoard_id(boardDto.getId());
                            updateBoardFileDto.setFilestatus("U");

                            uFileList.add(updateBoardFileDto);
                        }
                    });
                } else if(boardFileDto.getFilestatus().equalsIgnoreCase("D")) {
                    BoardFileDto deleteBoardFileDto = new BoardFileDto();

                    deleteBoardFileDto.setId(boardFileDto.getId());
                    deleteBoardFileDto.setBoard_id(boardDto.getId());
                    deleteBoardFileDto.setFilestatus("D");

                    fileUtils.deleteFile("board/", boardFileDto.getFilename());

                    uFileList.add(deleteBoardFileDto);
                }
            });
        }

        if(uploadFiles != null && uploadFiles.length > 0) {
            Arrays.stream(uploadFiles).forEach(file -> {
                if(!file.getOriginalFilename().equalsIgnoreCase("")
                        && file.getOriginalFilename() != null) {
                    BoardFileDto addBoardFileDto = fileUtils.parserFileInfo(file, "board/");

                    addBoardFileDto.setBoard_id(boardDto.getId());
                    addBoardFileDto.setFilestatus("I");

                    uFileList.add(addBoardFileDto);
                }
            });
        }

        Board board = boardRepository.findById(boardDto.getId()).orElseThrow(
                () -> new RuntimeException("board not exist")
        );

        board.setTitle(boardDto.getTitle());
        board.setContent(boardDto.getContent());
        board.setModdate(LocalDateTime.now());

        uFileList.forEach(
                boardFileDto -> {
                    if(boardFileDto.getFilestatus().equalsIgnoreCase("U")
                            || boardFileDto.getFilestatus().equalsIgnoreCase("I")) {
                        board.getBoardFileList().add(boardFileDto.toEntity(board));
                    } else if(boardFileDto.getFilestatus().equalsIgnoreCase("D")) {
                        boardFileRepository.delete(boardFileDto.toEntity(board));
                    }
                }
        );

        boardRepository.save(board);

        boardRepository.flush();

        return boardRepository.findById(boardDto.getId()).orElseThrow(
                () -> new RuntimeException("board not exist")
        ).toDto();
    }
}
