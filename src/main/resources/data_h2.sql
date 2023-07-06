INSERT INTO ROLE (name) values ('ROLE_ADMIN');
INSERT INTO ROLE (name) values ('ROLE_MEMBER');
INSERT INTO MEMBER (username, nickname, password, phone_number, phone_auth, provider_type_code, coin, rating, review_count) VALUES ('user1', 'User1', 'password123', '01012345678', 1, 'kakao', 1000, 3.5, 1);
INSERT INTO member_roles (member_id, role_id) VALUES (1, 2);
INSERT INTO MEMBER (username, nickname, password, phone_number, phone_auth, provider_type_code, coin, rating, review_count) VALUES ('user2', 'User2', 'password123', '01023456789', 1, 'kakao', 0, 5.0, 1);
INSERT INTO member_roles (member_id, role_id) VALUES (2, 2);
INSERT INTO MEMBER (username, nickname, password, phone_number, phone_auth, provider_type_code, coin, rating, review_count) VALUES ('user3', 'User3', 'password123', '01034567890', 1, 'kakao', 0, 1.0, 2);
INSERT INTO member_roles (member_id, role_id) VALUES (3, 2);
INSERT INTO MEMBER (username, nickname, password, phone_number, phone_auth, provider_type_code, coin, rating, review_count) VALUES ('user4', 'User4', 'password123', '01045678901', 1, 'kakao', 0, 3.5, 2);
INSERT INTO member_roles (member_id, role_id) VALUES (4, 2);
INSERT INTO MEMBER (username, nickname, password, phone_number, phone_auth, provider_type_code, coin, rating, review_count) VALUES ('user5', 'User5', 'password123', '01056789012', 1, 'kakao', 0, 0, 0);
INSERT INTO member_roles (member_id, role_id) VALUES (5, 2);
INSERT INTO MEMBER (username, nickname, password, phone_number, phone_auth, provider_type_code, coin, rating, review_count) VALUES ('admin', 'admin', 'password123', '01026749612', 1, 'kakao', 0, 0, 0);
INSERT INTO member_roles (member_id, role_id) VALUES (6, 1);
INSERT INTO member_roles (member_id, role_id) VALUES (6, 2);
INSERT INTO MEMBER (username, nickname, password, phone_number, phone_auth, provider_type_code, coin, rating, review_count) VALUES ('KAKAO__12345678', 'bigsand', 'password12311', '01053833333', 1, 'kakao', 0, 0, 0);
INSERT INTO member_roles (member_id, role_id) VALUES (7, 2);
INSERT INTO MEMBER (username, nickname, password, phone_number, phone_auth, provider_type_code, coin, rating, review_count) VALUES ('KAKAO__2863095386', 'donggi', 'password12311', '01000000000', 1, 'KAKAO', 0, 0, 0);
INSERT INTO member_roles (member_id, role_id) VALUES (8, 2);

INSERT INTO NOTIFICATION (requester_id, helper_id, message, type, created_at) VALUES (1, 2, '채팅시작테스트', 'START', NOW());
INSERT INTO NOTIFICATION (requester_id, helper_id, message, type, created_at) VALUES (1, 3, '거래취소테스트', 'CANCEL', NOW());
INSERT INTO NOTIFICATION (requester_id, helper_id, message, type, created_at) VALUES (3, 4, '거래완료테스트', 'END', NOW());
INSERT INTO NOTIFICATION (requester_id, helper_id, message, type, created_at) VALUES (6, 1, '큰모래님과의 채팅이 시작되었습니다.', 'START', NOW());
INSERT INTO NOTIFICATION (requester_id, helper_id, message, type, created_at) VALUES (2, 6, '큰모래님과의 거래가 취소되었습니다.', 'CANCEL', NOW());
INSERT INTO NOTIFICATION (requester_id, helper_id, message, type, created_at) VALUES (6, 3, '큰모래님과의 거래가 완료되었습니다.', 'END', NOW());

INSERT INTO BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES (1000, 126.732240699017, 37.5110124805355, '나급해요', '내용1', '2823710500', '제목1', '오프라인', 1);
INSERT INTO BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES (1000, 126.732240699017, 37.5110124805355, '나급해요', '내용2', '2823710500', '제목2', '오프라인', 1);
INSERT INTO BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES (1000, 126.732240699017, 37.5110124805355, '나급해요', '내용3', '2823710500', '제목3', '오프라인', 1);
INSERT INTO BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES (1000, 126.732240699017, 37.5110124805355, '나급해요', '내용4', '2823710500', '제목4', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용5', '2823710500', '제목5', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용6', '2823710500', '제목6', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용7', '2823710500', '제목7', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용8', '2823710500', '제목8', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용9', '2823710500', '제목9', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용10', '2823710500', '제목10', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용11', '2823710500', '제목11', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용12', '2823710500', '제목12', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용13', '2823710500', '제목13', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용14', '2823710500', '제목14', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용15', '2823710500', '제목15', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용16', '2823710500', '제목16', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용17', '2823710500', '제목17', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용18', '2823710500', '제목18', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용19', '2823710500', '제목19', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나잘해요', '내용20', '2823710500', '제목20', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용21', '2823710500', '제목21', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용22', '2823710500', '제목22', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용23', '2823710500', '제목23', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용24', '2823710500', '제목24', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용25', '2823710500', '제목25', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용26', '2823710500', '제목26', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나잘해요', '내용27', '2823710500', '제목27', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용28', '2823710500', '제목28', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용29', '2823710500', '제목29', '오프라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(1000, 126.732240699017, 37.5110124805355, '나급해요', '내용30', '2823710500', '제목30', '오프라인', 1);


-- //trade_type이 온라인인 경우의 게시물 20개
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(10, 126.732240699017, 37.5110124805355, '나급해요', '내용31', '2823710500', '제목31', '온라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(100, 126.732240699017, 37.5110124805355, '나급해요', '내용32', '2823710500', '제목32', '온라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(10, 126.732240699017, 37.5110124805355, '나급해요', '내용33', '2823710500', '제목33', '온라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(100, 126.732240699017, 37.5110124805355, '나급해요', '내용34', '2823710500', '제목34', '온라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(100, 126.732240699017, 37.5110124805355, '나급해요', '내용35', '2823710500', '제목35', '온라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(20, 126.732240699017, 37.5110124805355, '나급해요', '내용36', '2823710500', '제목36', '온라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(200, 126.732240699017, 37.5110124805355, '나급해요', '내용37', '2823710500', '제목37', '온라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(100, 126.732240699017, 37.5110124805355, '나급해요', '내용38', '2823710500', '제목38', '온라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(10, 126.732240699017, 37.5110124805355, '나급해요', '내용39', '2823710500', '제목39', '온라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(10, 126.732240699017, 37.5110124805355, '나급해요', '내용40', '2823710500', '제목40', '온라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(10, 126.732240699017, 37.5110124805355, '나급해요', '내용41', '2823710500', '제목41', '온라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(10, 126.732240699017, 37.5110124805355, '나급해요', '내용42', '2823710500', '제목42', '온라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(10, 126.732240699017, 37.5110124805355, '나급해요', '내용43', '2823710500', '제목43', '온라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(10, 126.732240699017, 37.5110124805355, '나급해요', '내용44', '2823710500', '제목44', '온라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(10, 126.732240699017, 37.5110124805355, '나급해요', '내용45', '2823710500', '제목45', '온라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(10, 126.732240699017, 37.5110124805355, '나급해요', '내용46', '2823710500', '제목46', '온라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(10, 126.732240699017, 37.5110124805355, '나급해요', '내용47', '2823710500', '제목47', '온라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(10, 126.732240699017, 37.5110124805355, '나급해요', '내용48', '2823710500', '제목48', '온라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(10, 126.732240699017, 37.5110124805355, '나급해요', '내용49', '2823710500', '제목49', '온라인', 1);
insert into BOARD (reward_coin, x, y, board_type, content, reg_code, title, trade_type, member_id) VALUES(10, 126.732240699017, 37.5110124805355, '나급해요', '내용50', '2823710500', '제목50', '온라인', 1);



INSERT INTO TRADE_STATUS (board_id, requester_id, helper_id, status) VALUES (1, 1, 2, 'BEFORE');
INSERT INTO TRADE_STATUS (board_id, requester_id, helper_id, status) VALUES (2, 1, 3, 'CANCELED');
INSERT INTO TRADE_STATUS (board_id, requester_id, helper_id, status) VALUES (3, 2, 3, 'COMPLETE');
INSERT INTO TRADE_STATUS (board_id, requester_id, helper_id, status) VALUES (4, 3, 4, 'COMPLETE');
INSERT INTO TRADE_STATUS (board_id, requester_id, helper_id, status) VALUES (null, 6, 4, 'COMPLETE');
INSERT INTO TRADE_STATUS (board_id, requester_id, helper_id, status) VALUES (null, 3, 6, 'COMPLETE');
INSERT INTO TRADE_STATUS (board_id, requester_id, helper_id, status) VALUES (null, 6, 4, 'BEFORE');
INSERT INTO TRADE_STATUS (board_id, requester_id, helper_id, status) VALUES (null, 3, 6, 'CANCELED');

INSERT INTO REVIEW (content, rating, trade_status_id, writer_id, receiver_id) VALUES ('내용1', 1.0, 1, 1, 2);
INSERT INTO REVIEW (content, rating, trade_status_id, writer_id, receiver_id) VALUES ('내용2', 1.0, 2, 1, 3);
INSERT INTO REVIEW (content, rating, trade_status_id, writer_id, receiver_id) VALUES ('내용3', 2.0, 2, 3, 1);
INSERT INTO REVIEW (content, rating, trade_status_id, writer_id, receiver_id) VALUES ('내용4', 3.0, 3, 2, 3);

INSERT INTO EXCHANGE (member_id, bank_type, account_number, holder_name, money, status) VALUES (1, '국민', '123456789', '홍길동', 1000, 0);
INSERT INTO EXCHANGE (member_id, bank_type, account_number, holder_name, money, status) VALUES (1, '국민', '123456789', '홍길동', 1000, 1);