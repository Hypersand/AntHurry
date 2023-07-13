package com.ant.hurry.boundedContext.tradeStatus.dto;


import com.ant.hurry.boundedContext.board.entity.Board;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.entity.ProfileImage;
import com.ant.hurry.boundedContext.review.entity.Review;
import com.ant.hurry.boundedContext.tradeStatus.entity.Status;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class TradeStatusDto {

    private Long id;
    private Status status;
    private Member requester;
    private Member helper;
    private Board board;
    private boolean reviewCheck;
    private LocalDateTime createdAt;
    private String fullPath;

    public TradeStatusDto(TradeStatus tradeStatus, Member member){
        this.id = tradeStatus.getId();
        this.status = tradeStatus.getStatus();
        this.createdAt = tradeStatus.getCreatedAt();
        this.board = tradeStatus.getBoard();
        this.helper = tradeStatus.getHelper();
        this.requester = tradeStatus.getRequester();
        this.reviewCheck = checkReview(tradeStatus.getReviewList(), member);
        this.fullPath = null;
    }

    public void setFullPath(String fullPath){
        this.fullPath = fullPath;
    }

    private boolean checkReview(List<Review> reviewList, Member member) {
        if(reviewList.isEmpty()){
            return false;
        }else{
            return reviewList.get(0).getWriter().equals(member) || reviewList.size() == 2;
        }
    }
}
