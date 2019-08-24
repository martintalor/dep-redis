--在opreate表中有c12的将global表中的state数据改为02
create or replace procedure FixGlobalStateJob is
        total NUMBER;
        CURSOR packageCursor IS
        SELECT DISTINCT p.package_id, p.to_node_id, p.create_time
        FROM NODE_OPERATE_STATE n
        INNER JOIN DATA_NODE_PROCESS p ON p.process_id=n.process_id
        WHERE n.operate_state_dm='C12';

begin
FOR packageCur IN packageCursor
 LOOP
    SELECT COUNT(*) INTO total FROM Package_Global_State ss WHERE ss.to_node_id=packageCur.to_node_id AND ss.package_id LIKE REGEXP_SUBSTR(packageCur.Package_Id,'[^.]+',1,1)||'%';
    IF total>0 THEN
        UPDATE Package_Global_State s
        SET s.global_state_dm='02'
        WHERE s.to_node_id=packageCur.to_node_id AND s.package_id LIKE REGEXP_SUBSTR(packageCur.Package_Id,'[^.]+',1,1)||'%';
    ELSE
        INSERT INTO Package_Global_State VALUES (REGEXP_SUBSTR(packageCur.Package_Id,'[^.]+',1,1),
        packageCur.to_node_id,packageCur.to_node_id,'02',packageCur.create_time,sysdate,'');
    END IF;
 END LOOP;
end;
/



--声明定时任务，注意与上面的存储过程分开执行。
declare
    jobno number;
begin
    sys.dbms_job.submit(job => jobno,
    what => 'FixGlobalStateJob;',                          --执行的存储过程的名字
    next_date => sysdate,
    interval =>'sysdate+60/86400');            --每天86400秒钟，即60秒钟运行prc_name过程一次
    commit;
end;
/