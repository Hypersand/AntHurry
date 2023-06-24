INSERT INTO MEMBER (username, nickname, password, phone_number, provider_type_code, coin) VALUES ('user1', 'User1', 'password123', '01012345678', 'kakao', 0);
INSERT INTO MEMBER (username, nickname, password, phone_number, provider_type_code, coin) VALUES ('user2', 'User2', 'password123', '01023456789', 'kakao', 0);
INSERT INTO MEMBER (username, nickname, password, phone_number, provider_type_code, coin) VALUES ('user3', 'User3', 'password123', '01034567890', 'kakao', 0);
INSERT INTO MEMBER (username, nickname, password, phone_number, provider_type_code, coin) VALUES ('user4', 'User4', 'password123', '01045678901', 'kakao', 0);
INSERT INTO MEMBER (username, nickname, password, phone_number, provider_type_code, coin) VALUES ('admin', 'Admin', 'password123', '01056789012', 'kakao', 0);

INSERT INTO NOTIFICATION (requester_id, helper_id, message, type) VALUES (1, 2, '채팅시작테스트', 'START');
INSERT INTO NOTIFICATION (requester_id, helper_id, message, type) VALUES (1, 3, '거래파기테스트', 'CANCEL');
INSERT INTO NOTIFICATION (requester_id, helper_id, message, type) VALUES (3, 4, '거래완료테스트', 'END');

INSERT INTO BOARD (title, contents, board_type, x, y, reward_coin, reg_code) VALUES ('제목1', '내용1', '나급해요', 123.127, 37.123, 0, 12345678);
INSERT INTO BOARD (title, contents, board_type, x, y, reward_coin, reg_code) VALUES ('제목2', '내용2', '나급해요', 123.127, 37.123, 0, 12345678);
INSERT INTO BOARD (title, contents, board_type, x, y, reward_coin, reg_code) VALUES ('제목3', '내용3', '나급해요', 123.127, 37.123, 0, 12345678);
INSERT INTO BOARD (title, contents, board_type, x, y, reward_coin, reg_code) VALUES ('제목4', '내용4', '나급해요', 123.127, 37.123, 0, 12345678);


INSERT INTO TRADE_STATUS (board_id, requester_id, helper_id, status) VALUES (1, 1, 2, 'BEFORE');
INSERT INTO TRADE_STATUS (board_id, requester_id, helper_id, status) VALUES (2, 1, 3, 'CANCELED');
INSERT INTO TRADE_STATUS (board_id, requester_id, helper_id, status) VALUES (3, 2, 3, 'COMPLETE');
INSERT INTO TRADE_STATUS (board_id, requester_id, helper_id, status) VALUES (4, 3, 4, 'INPROGRESS');

INSERT INTO REVIEW (content, rating, trade_status_id, writer_id) VALUES ('내용1', 1.0, 1, 1);
INSERT INTO REVIEW (content, rating, trade_status_id, writer_id) VALUES ('내용2', 2.0, 2, 3);
INSERT INTO REVIEW (content, rating, trade_status_id, writer_id) VALUES ('내용3', 3.0, 3, 2);

