package com.example.board.dto;

import lombok.Data;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * JPA를 이용하는 Repository에서는 페이지 처리 결과를 Page<Entity> 타입으로 변환하므로
 * 서비스 계층에서 이를 처리하기 위해서는 별도의 클래스를 만듦
 * 1) Page<Entity>의 엔티티 객체들을 DTO 객체로 변환해서 자료 구조로 담고,
 * 2) 화면 출력에 필요한 페이지 정보들을 구성
 */

@Getter
public class PageResultDTO<DTO,EN>{

    // DTO리스트
    private List<DTO> dtoList;

    // 총 페이지 번호
    private int totalPage;

    // 현재 페이지 번호
    private int page;

    // 목록 사이즈
    private int size;

    // 시작페이지,끝페이지 번호
    private int start,end;

    // 이전, 다음
    private boolean prev, next;

    // 페이지 번호 목록
    private List<Integer> pageList;

    // Function<EN,DTO> : 엔티티 객체들을 DTO로 변환해주는 기능
    public PageResultDTO(Page<EN> result, Function<EN,DTO> fn){
        dtoList = result.stream().map(fn).collect(Collectors.toList());

        totalPage = result.getTotalPages();

        makePageList(result.getPageable());
    }

    private void makePageList(Pageable pageable){
        this.page = pageable.getPageNumber() + 1 ; // 0부터 시작하므로 1을 더해준다
        this.size = pageable.getPageSize();

        // temp end page
        // 끝번호를 미리 계산하는 이유 : 시작번호 계산 수월하게 하기위해
        int tempEnd = (int)(Math.ceil(page / 10.0)) * 10;

        start = tempEnd - 9;

        prev = start > 1;

        end = totalPage > tempEnd ? tempEnd : totalPage;

        next = totalPage > tempEnd;

        pageList = IntStream.rangeClosed(start,end).boxed().collect(Collectors.toList());
    }
}