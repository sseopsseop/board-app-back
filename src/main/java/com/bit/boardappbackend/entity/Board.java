package com.bit.boardappbackend.entity;

import com.bit.boardappbackend.dto.BoardDto;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@SequenceGenerator(
        name = "boardSeqGenerator",
        sequenceName = "BOARD_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "boardSeqGenerator"
    )
    private Long id;
    private String title;
    private String content;
    @ManyToOne
    @JoinColumn(name = "writer_id", referencedColumnName = "id")
    private Member member;
    private LocalDateTime regdate;
    private LocalDateTime moddate;
    private int cnt;
    @Transient
    private String searchKeyword;
    @Transient
    private String searchCondition;
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<BoardFile> boardFileList;


    public BoardDto toDto() {
        return BoardDto.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .writer_id(this.member.getId())
                .nickname(this.member.getNickname())
                .regdate(this.regdate)
                .moddate(this.moddate)
                .cnt(this.cnt)
                .searchKeyword(this.searchKeyword)
                .searchCondition(this.searchCondition)
                .boardFileDtoList(
                        boardFileList != null && boardFileList.size() > 0
                            ? boardFileList.stream().map(BoardFile::toDto).toList()
                            : new ArrayList<>()
                )
                .build();
    }













}
