package com.bit.boardappbackend.dto;

import com.bit.boardappbackend.entity.Board;

import com.bit.boardappbackend.entity.BoardFile;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BoardFileDto {
    private Long id;
    private Long board_id;
    private String filename;
    private String filepath;
    private String fileoriginname;
    private String filetype;
    private String filestatus;
    private String newfilename;

    public BoardFile toEntity(Board board) {
        return BoardFile.builder()
                .id(this.id)
                .board(board)
                .filename(this.filename)
                .filepath(this.filepath)
                .fileoriginname(this.fileoriginname)
                .filetype(this.filetype)
                .filestatus(this.filestatus)
                .newfilename(this.newfilename)
                .build();
    }
}
