package com.adminpanel.merchantadminpanel.qrgeneration.dao;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.adminpanel.dbconnection.application.DBCPNewConnection;
import com.adminpanel.merchantadminpanel.qrgeneration.bo.BanglaQRGenerationBO;


public class BanglaQRGenerationDAO  {
	/*
	 * PROCEDURE dpr_qrgen_account_title (
      in_terminalid   IN       VARCHAR2,
      in_ipimeno      IN       VARCHAR2,
      in_mailid       IN       VARCHAR2,
      in_sessionid    IN       VARCHAR2,
      in_compid       IN       VARCHAR2,
      in_userid       IN       VARCHAR2,
      in_actnum       IN       VARCHAR2,
      out_actitle     OUT      VARCHAR2,
      out_code        OUT      INTEGER,
      out_message     OUT      VARCHAR2
   )
	 */
	public BanglaQRGenerationBO getMenuCheckPro(String sUserID, String sSessionID, String sCompanyID,
			String sBranchID, String sRemoteIPAddress, String sActionPath) throws Exception {
		Connection oConn = null;
		oConn = DBCPNewConnection.getConnection();
		BanglaQRGenerationBO oBanglaQRGenerationBO = new BanglaQRGenerationBO();
		CallableStatement oStmt = oConn.prepareCall("{call ibmm.dpr_ibk_main_menu_check(?,?,?,?,?,?,?,?)}");
		oStmt.setString(1, sUserID);
		oStmt.setString(2, sSessionID);
		oStmt.setString(3, sCompanyID);
		oStmt.setString(4, sBranchID);
		oStmt.setString(5, sRemoteIPAddress);
		oStmt.setString(6, sActionPath);
		oStmt.registerOutParameter(7, java.sql.Types.INTEGER);
		oStmt.registerOutParameter(8, java.sql.Types.VARCHAR);
		try {
			oStmt.execute();
			oBanglaQRGenerationBO.setErrorCode("" + oStmt.getInt(7));
			oBanglaQRGenerationBO.setErrorMessage(oStmt.getString(8));
		} catch (Exception e) {
			e.printStackTrace();
			try {
				oConn.rollback();
			} catch (Exception E) {
			}
		} finally {

			DBCPNewConnection.releaseConnection(oConn);
		}
		return oBanglaQRGenerationBO;
	}
	public BanglaQRGenerationBO getExecute(
											String terminalid, 
											String ipimeno, 
											String mailid,
											String sessionid,
											String compid,
											String userid,
											String actnum
											) throws Exception {
		System.out.println("Input account no"+actnum);

		Connection oConn = null;
		System.out.println("i am in  proc");
		oConn = DBCPNewConnection.getConnection();
		BanglaQRGenerationBO oBanglaQRGenerationBO = new BanglaQRGenerationBO();
		CallableStatement oStmt = oConn.prepareCall("{call ibmm.dpr_qrgen_account_title(?,?,?,?,?,?,?,?,?,?,?)}");
		oStmt.setString(1, terminalid);
		oStmt.setString(2, ipimeno);
		oStmt.setString(3, mailid);
		oStmt.setString(4, sessionid);
		oStmt.setString(5, compid);
		oStmt.setString(6, userid);
		oStmt.setString(7, actnum);
		oStmt.registerOutParameter(8, java.sql.Types.VARCHAR);
		oStmt.registerOutParameter(9, java.sql.Types.VARCHAR);
		oStmt.registerOutParameter(10, java.sql.Types.INTEGER);
		oStmt.registerOutParameter(11, java.sql.Types.VARCHAR);
		try {
			oStmt.execute();
			System.out.println("===>>>> "+oStmt.getString(8)+" --- "+oStmt.getString(9)+" --- "+(oStmt.getInt(10)));
			oBanglaQRGenerationBO.setAccountTitle(oStmt.getString(8));
			oBanglaQRGenerationBO.setAddress(oStmt.getString(9));
			oBanglaQRGenerationBO.setErrorCode("" + oStmt.getInt(10));
			oBanglaQRGenerationBO.setErrorMessage(oStmt.getString(11));
		} catch (Exception e) {
			e.printStackTrace();
			try {
				oConn.rollback();
			} catch (Exception E) {
			}
		} finally {
			if (oStmt != null) {
				oStmt.close();
			}
			DBCPNewConnection.releaseConnection(oConn);
		}
		return oBanglaQRGenerationBO;
	}
		  
	  
	  public BanglaQRGenerationBO getExecuteAPI(String actnum) throws Exception {
	      BanglaQRGenerationBO oBanglaQRGenerationBO = new BanglaQRGenerationBO();
	      String sRequestString = "";
	      String sResponseString = "";
	      String sAuthString = "";
	      String errorCode = "";
	      String sCustomerPhoto = "";
	      HttpResponse httpResponse = null;
	      HttpGet post = null;

	      try {
	       // String url = "http://10.88.14.10:7001/ords/apiservice/baabsv1/account-holder-photo/" + actnum;
	    	   String url ="http://10.11.201.170:8085/ords/apiservice/baabsv1/account-holder-photo/"+ actnum;
	         CloseableHttpClient client = HttpClients.createDefault();
	         post = new HttpGet(url);
	         post.setHeader("Content-Type", "application/json");
	         httpResponse = client.execute(post);
	         int StatusCode = httpResponse.getStatusLine().getStatusCode();
	         if (StatusCode == 200) {
	            sResponseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

	            try {
	               JSONObject jsonResponse = new JSONObject(sResponseString);
	               sCustomerPhoto = jsonResponse.getString("customer_photo");
	               byte[] decodedBytes = Base64.getDecoder().decode(sCustomerPhoto);
	               System.out.println("Decoded upload data : " + decodedBytes.length);
	               String uploadFile = "D:\\QR\\";
	               System.out.println("===>>>>" + uploadFile + actnum + ".png");
	               Files.write(Paths.get(uploadFile + actnum + ".png"), decodedBytes);
	               oBanglaQRGenerationBO.setErrorCode("0");
	            } catch (Exception var16) {
	               var16.printStackTrace();
	            }
	         } else {
	            oBanglaQRGenerationBO.setErrorCode("1");
	            oBanglaQRGenerationBO.setErrorMessage("User Image not found...!");
	            System.out.println("Server not Found.." + StatusCode);
	         }
	      } catch (Exception var17) {
	         oBanglaQRGenerationBO.setErrorCode("1");
	         oBanglaQRGenerationBO.setErrorMessage("Server not Found.. Please check Internet Connection...");
	      }

	      return oBanglaQRGenerationBO;
	   }
	 


}
