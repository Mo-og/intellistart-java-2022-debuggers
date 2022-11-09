insert into USERS(ID, EMAIL, ROLE)
values ((select nextval('USER_SEQUENCE')), 'interviewer@test.do', 'INTERVIEWER');
insert into USERS(ID, EMAIL, ROLE)
values ((select nextval('USER_SEQUENCE')), 'interviewercandidate@test.do', 'CANDIDATE');