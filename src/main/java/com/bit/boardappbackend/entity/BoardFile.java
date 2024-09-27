package com.bit.boardappbackend.entity;

import com.bit.boardappbackend.dto.BoardFileDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@SequenceGenerator(
        name = "boardFileSeqGenerator",
        sequenceName = "BOARD_FILE_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardFile {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "boardFileSeqGenerator"
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id", referencedColumnName = "id")
    @JsonBackReference
    private Board board;

    private String filename;
    private String filepath;
    private String fileoriginname;
    private String filetype;
    @Transient
    private String filestatus;
    @Transient
    private String newfilename;

    public BoardFileDto toDto() {
        return BoardFileDto.builder()
                .id(this.id)
                .board_id(this.board.getId())
                .filename(this.filename)
                .filepath(this.filepath)
                .filetype(this.filetype)
                .fileoriginname(this.fileoriginname)
                .filestatus(this.filestatus)
                .newfilename(this.newfilename)
                .build();
    }









}
