--                                                                                                                 
-- PRUGE SCRIPTS
--

CREATE GLOBAL TEMPORARY TABLE request_cleanup_tmp ( id_nbr NUMBER ) ON COMMIT DELETE ROWS;




CREATE OR REPLACE PROCEDURE cleanup_web_request(reqId NUMBER) 
AUTHID CURRENT_USER 
IS
	v_code NUMBER;
	v_errm VARCHAR2(64);
BEGIN

  DBMS_OUTPUT.PUT_LINE('Starting to remove request ' || TO_CHAR(reqId) );

	INSERT INTO request_cleanup_tmp ( SELECT proc_request_id FROM web_request_requests WHERE web_request_id = reqId );
	DBMS_OUTPUT.PUT_LINE('Found ' || TO_CHAR(SQL%ROWCOUNT) || ' accounts to remove.');
	
	DELETE FROM proc_request_infile WHERE request_id IN ( SELECT id_nbr FROM request_cleanup_tmp );
	DBMS_OUTPUT.PUT_LINE('Removed ' || TO_CHAR(SQL%ROWCOUNT) || ' rows from input file table.');
	
	DELETE FROM proc_request_info   WHERE request_id IN ( SELECT id_nbr FROM request_cleanup_tmp );
	DBMS_OUTPUT.PUT_LINE('Removed ' || TO_CHAR(SQL%ROWCOUNT) || ' rows from account info table.');

  DELETE FROM proc_outfile_attrs where file_id IN (
    SELECT file_id FROM proc_request_outfile WHERE trail_id IN ( 
       SELECT trail_id FROM proc_request_trail WHERE request_id IN (SELECT id_nbr FROM request_cleanup_tmp)
    )
  );
	DBMS_OUTPUT.PUT_LINE('Removed ' || TO_CHAR(SQL%ROWCOUNT) || ' rows from output attrs file table.');

  DELETE FROM proc_request_outfile WHERE trail_id IN ( 
       SELECT trail_id FROM proc_request_trail WHERE request_id IN (SELECT id_nbr FROM request_cleanup_tmp)
  );
	DBMS_OUTPUT.PUT_LINE('Removed ' || TO_CHAR(SQL%ROWCOUNT) || ' rows from output file table.');
	
	DELETE FROM proc_request_trail  WHERE request_id IN ( SELECT id_nbr FROM request_cleanup_tmp );
	DBMS_OUTPUT.PUT_LINE('Removed ' || TO_CHAR(SQL%ROWCOUNT) || ' rows from account status history table.');
  	
	DELETE FROM web_request_requests WHERE web_request_id = reqId;
	DBMS_OUTPUT.PUT_LINE('Removed ' || TO_CHAR(SQL%ROWCOUNT) || ' rows from account-relation table.');
  
	DELETE FROM proc_request WHERE request_id IN ( SELECT id_nbr FROM request_cleanup_tmp );
	DBMS_OUTPUT.PUT_LINE('Removed ' || TO_CHAR(SQL%ROWCOUNT) || ' rows from account table.');

	DELETE FROM web_bundlefile WHERE web_request_id = reqId;
	DBMS_OUTPUT.PUT_LINE('Removed ' || TO_CHAR(SQL%ROWCOUNT) || ' rows from report files table.');

	DELETE FROM web_notification WHERE web_request_id = reqId;
	DBMS_OUTPUT.PUT_LINE('Removed ' || TO_CHAR(SQL%ROWCOUNT) || ' rows from notification table.');

	DELETE FROM web_request_info WHERE web_request_id = reqId;
	DBMS_OUTPUT.PUT_LINE('Removed ' || TO_CHAR(SQL%ROWCOUNT) || ' rows from request info table.');

	DELETE FROM BCK_RULE_EXEC_HIST WHERE request_uid = reqId;
 	DBMS_OUTPUT.PUT_LINE('Removed ' || TO_CHAR(SQL%ROWCOUNT) || ' rows from rule exec history table.');

	DELETE FROM web_request WHERE request_id = reqId;
	DBMS_OUTPUT.PUT_LINE('Removed ' || TO_CHAR(SQL%ROWCOUNT) || ' rows from request table.');
	
	COMMIT;

	-- reindexing web tables
	DBMS_OUTPUT.PUT_LINE('Reindexing account-related tables.');
	EXECUTE IMMEDIATE 'ANALYZE TABLE web_bundlefile COMPUTE STATISTICS FOR ALL INDEXES FOR ALL COLUMNS';
	EXECUTE IMMEDIATE 'ANALYZE TABLE web_request_info COMPUTE STATISTICS FOR ALL INDEXES FOR ALL COLUMNS';
	EXECUTE IMMEDIATE 'ANALYZE TABLE web_request_requests COMPUTE STATISTICS FOR ALL INDEXES FOR ALL COLUMNS';
		
	-- reindexing tables from request_base 
	DBMS_OUTPUT.PUT_LINE('Reindexing request-related tables.');
	EXECUTE IMMEDIATE 'ANALYZE TABLE proc_request_infile COMPUTE STATISTICS FOR ALL INDEXES FOR ALL COLUMNS';
	EXECUTE IMMEDIATE 'ANALYZE TABLE proc_request_info COMPUTE STATISTICS FOR ALL INDEXES FOR ALL COLUMNS';
	EXECUTE IMMEDIATE 'ANALYZE TABLE proc_request_trail COMPUTE STATISTICS FOR ALL INDEXES FOR ALL COLUMNS';
	EXECUTE IMMEDIATE 'ANALYZE TABLE proc_request COMPUTE STATISTICS FOR ALL INDEXES FOR ALL COLUMNS';
	
	DBMS_OUTPUT.PUT_LINE('Finished removing request ' || TO_CHAR(reqId) );
	
	EXCEPTION
	
		WHEN OTHERS THEN
			ROLLBACK;
			v_code := SQLCODE;
			v_errm := SUBSTR(SQLERRM, 1 , 64);
			DBMS_OUTPUT.PUT_LINE('Error removing request ' || TO_CHAR(reqId) );
			DBMS_OUTPUT.PUT_LINE('Error code: ' || v_code );
			DBMS_OUTPUT.PUT_LINE('Error message: ' || v_errm );
	
END;
/


CREATE OR REPLACE PROCEDURE cleanup_consequences(reqId NUMBER) 
AUTHID CURRENT_USER 
IS
	v_code NUMBER;
	v_errm VARCHAR2(64);
BEGIN

  	DBMS_OUTPUT.PUT_LINE('Starting to remove consequences for ' || TO_CHAR(reqId) );

	INSERT INTO request_cleanup_tmp ( SELECT attribute_uid FROM bck_consequence WHERE transaction_id = reqId );	

	DELETE FROM bck_consequence WHERE transaction_id = reqId;
	DBMS_OUTPUT.PUT_LINE('Removed ' || TO_CHAR(SQL%ROWCOUNT) || ' rows from rules consequence table.');

	DELETE FROM bck_consequence_attr WHERE objid IN ( SELECT id_nbr FROM request_cleanup_tmp );
	DBMS_OUTPUT.PUT_LINE('Removed ' || TO_CHAR(SQL%ROWCOUNT) || ' rows from consequence attribute table.');

	DELETE FROM bck_invoice_fact WHERE transaction_id = reqId;
	DBMS_OUTPUT.PUT_LINE('Removed ' || TO_CHAR(SQL%ROWCOUNT) || ' rows from invoice data table.');

	DELETE FROM bck_contract_totals_fact WHERE transaction_id = reqId;
	DBMS_OUTPUT.PUT_LINE('Removed ' || TO_CHAR(SQL%ROWCOUNT) || ' rows from contract data table.');
		
	COMMIT;

	-- reindexing web tables
	DBMS_OUTPUT.PUT_LINE('Reindexing consequence-related tables.');
	EXECUTE IMMEDIATE 'ANALYZE TABLE bck_consequence COMPUTE STATISTICS FOR ALL INDEXES FOR ALL COLUMNS';
	EXECUTE IMMEDIATE 'ANALYZE TABLE bck_consequence_attr COMPUTE STATISTICS FOR ALL INDEXES FOR ALL COLUMNS';
	EXECUTE IMMEDIATE 'ANALYZE TABLE bck_invoice_fact COMPUTE STATISTICS FOR ALL INDEXES FOR ALL COLUMNS';
	EXECUTE IMMEDIATE 'ANALYZE TABLE bck_contract_totals_fact COMPUTE STATISTICS FOR ALL INDEXES FOR ALL COLUMNS';
	
	DBMS_OUTPUT.PUT_LINE('Finished removing consequences for ' || TO_CHAR(reqId) );
	
	EXCEPTION
	
		WHEN OTHERS THEN
			ROLLBACK;
			v_code := SQLCODE;
			v_errm := SUBSTR(SQLERRM, 1 , 64);
			DBMS_OUTPUT.PUT_LINE('Error removing request ' || TO_CHAR(reqId) );
			DBMS_OUTPUT.PUT_LINE('Error code: ' || v_code );
			DBMS_OUTPUT.PUT_LINE('Error message: ' || v_errm );
	
END;
/

CREATE OR REPLACE PROCEDURE cleanup_billcheckout(reqId NUMBER) 
AUTHID CURRENT_USER 
IS 
BEGIN
	DBMS_OUTPUT.PUT_LINE('Starting cleanup process.');
	cleanup_web_request(reqId);
	cleanup_consequences(reqId);
	DBMS_OUTPUT.PUT_LINE('Cleanup process finished.');
END;
/