package com.adminpanel.merchantadminpanel.qrgeneration.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.adminpanel.merchantadminpanel.qrgeneration.bo.BanglaQRGenerationBO;
import com.adminpanel.merchantadminpanel.qrgeneration.dao.BanglaQRGenerationDAO;
import com.adminpanel.merchantadminpanel.qrgeneration.formbean.BanglaQRGenerationForm;
import com.adminpanel.merchantadminpanel.utilitybill.report.bo.RebBillDetailsReportBO;
import com.adminpanel.utility.RemoveNullValue;
import com.adminpanel.utility.ReportManager;
import com.sun.javafx.collections.MappingChange.Map;

public class BanglaQRGenerationAction extends Action {
	public ActionForward execute(ActionMapping mapping,
			 ActionForm form,
			 HttpServletRequest request,
			 HttpServletResponse response)	throws Exception {
			 	



BanglaQRGenerationForm oBanglaQRGenerationForm=(BanglaQRGenerationForm) form;
BanglaQRGenerationBO oBanglaQRGenerationBO=new BanglaQRGenerationBO();
BanglaQRGenerationDAO oBanglaQRGenerationDAO =new BanglaQRGenerationDAO();
RemoveNullValue oRemoveNullValue = new RemoveNullValue();

String sActionPath = "";
sActionPath = mapping.getPath();
HttpSession session = request.getSession(true);
String sSuccessAction = "success";
String sFailureAction = "failure";
String sFatalErrorAction = "fatalError";
String sSessionExpireAction = "sessionExpire";
String sSessionMyBankMenuAction = "sessionMyBankMenu";
String sSuccess = sFatalErrorAction;
String sActionPathName = "";
String gsUserID = (String) session.getAttribute("GSUserID");
String gsUserTitle = (String) session.getAttribute("GSUserTitle");
String gsLastLogInDate =(String) session.getAttribute("GSLastLogInDate");
String gsLogInUserID = (String) session.getAttribute("GSLogInUserID");
String gsSessionID = (String) session.getAttribute("GSSessionID");
String gsInternalCardID =(String) session.getAttribute("GSInternalCardID");
String gsHeaderName = (String) session.getAttribute("GSHeaderName");
String gsHeaderLogIn = (String) session.getAttribute("GSHeaderLogIn");
String gsCompanyID = (String) session.getAttribute("GSCompanyCode");
String gsBranchID = (String) session.getAttribute("GSBranchCode");
String gsBranchName = (String) session.getAttribute("GSBranchName");
String gsTellerID = (String) session.getAttribute("GSTellerID");
String gsCompanyName = (String) session.getAttribute("GSCompanyName");
String gsBranchOpenDateDDFormat =(String) session.getAttribute("GSBranchOpenDateDDFormat");

//First Day of the Month	
Calendar calendar = Calendar.getInstance();		
calendar.set(Calendar.DAY_OF_MONTH, 1);
Date date = calendar.getTime();
SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
String firstDay = format1.format(date);

//Last Day of the Month
calendar.add(Calendar.MONTH, 1);  
calendar.set(Calendar.DAY_OF_MONTH, 1);  
calendar.add(Calendar.DATE, -1);  
Date lastDayOfMonth = calendar.getTime(); 
SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");  
String lastDay = format2.format(lastDayOfMonth);
String sBranchActionPathName ="/merchantadminpanel/banglaQRGeneration.do";

if (sActionPath.equals("/banglaQRGeneration")) {
	
	//System.out.println("===pppppppppppppppppp");
	session.setAttribute("oBanglaQRGenerationMessageBO"," ");
	oBanglaQRGenerationForm.setAccountNo("");
	oBanglaQRGenerationForm.setAccountTitle("");

   sSuccess = sSuccessAction;
}else if (sActionPath.equals("/banglaQRGenerationAccountInfo")) {
	session.setAttribute("oBanglaQRGenerationMessageBO"," ");
	oBanglaQRGenerationBO=oBanglaQRGenerationDAO.getExecute("ibanking", 
															"",
															gsUserID, 
															gsSessionID,
															"001",
															gsUserID, 
															oBanglaQRGenerationForm.getAccountNo());
	
//	System.out.println("ErrorCode ===>>>> "+oBanglaQRGenerationBO.getErrorCode());
	
	//System.out.println("Action "+oBanglaQRGenerationForm.getAccountNo()+" title"+oBanglaQRGenerationForm.getAccountTitle());
	
	if(oBanglaQRGenerationBO.getErrorCode().equals("0")) {
		oBanglaQRGenerationForm.setAccountTitle(oBanglaQRGenerationBO.getAccountTitle());
		oBanglaQRGenerationForm.setAddress(oBanglaQRGenerationBO.getAddress());
	sSuccess = sSuccessAction;
	
		}else if (oBanglaQRGenerationBO.getErrorCode().equals("1")) {
			
			String	BanglaQRGenerationMessageBO =oBanglaQRGenerationBO.getErrorMessage();							
			session.setAttribute("oBanglaQRGenerationMessageBO",BanglaQRGenerationMessageBO);
			
			sSuccess = sFailureAction;
		} 
		else if (oBanglaQRGenerationBO.getErrorCode().equals("2")) {
			clearSession(session);
			sSuccess = sSessionExpireAction;
		} 
		else if (oBanglaQRGenerationBO.getErrorCode().equals("3")) {
			clearSession(session);
			sSuccess = sSessionMyBankMenuAction;
		} 
		else {
			clearSession(session);
			sSuccess = sFatalErrorAction;
		}	
	}else if (sActionPath.equals("/executeBanglaQRGeneration")) {
		session.setAttribute("oBanglaQRGenerationMessageBO"," ");
		oBanglaQRGenerationBO = oBanglaQRGenerationDAO.getExecuteAPI(oBanglaQRGenerationForm.getAccountNo());
	
		if (oBanglaQRGenerationBO.getErrorCode().equals("0")) {
		
		 String length = "02";
			String value = "00";
			String  qr = "";
			qr +=getsr(TagLevel.PAYLOAD).toString()+ length + value;
			
			 value =  oBanglaQRGenerationForm.getAccountNo();//04934004355
		     length = getLent(value.length());
		     qr += getsr(TagLevel.MERCHANT_ACCOUNT_NO).toString() + length + oBanglaQRGenerationForm.getAccountNo();
		     // TRANSACTION CURRENCY
		     value = "050";
		     length = getLent(value.length());
		     qr += getsr(TagLevel.TRANSACTION_CURRENCY).toString() + length + value;
		     
		     //CRC
		     value = "70MM";
		     length = getLent(value.length());
		     qr += getsr(TagLevel.CRC).toString() + length + value;
		     
		     //COUNTRY_CODE
		     value = "BD";
		     length = getLent(value.length());
		     qr += getsr(TagLevel.COUNTRY_CODE).toString() + length + value;
		     
		     value =  oBanglaQRGenerationForm.getAccountTitle();//globalVariable.userName.toString();
		     length = getLent(value.length());
		     qr += getsr(TagLevel.MERCHANT_NAME).toString() + length + oBanglaQRGenerationForm.getAccountTitle();
		     String filePath = "D:\\QR\\UserInfo.png";
		   
		     System.out.println("print "+filePath);
		 	String charset = "UTF-8"; // or "ISO-8859-1"
		 	HashMap<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		 		
		   if(qr !="") {
		           createQRCode(qr, filePath, charset, hintMap, 550, 600);                
				   System.out.println("QR Code image created successfully!");
	
			   	
			//   System.out.println("Action "+oBanglaQRGenerationForm.getAccountNo()+"oBanglaQRGenerationForm.getAccountNo()"+oBanglaQRGenerationForm.getAccountTitle());
					
					  String sUserStatus = "";
					  String sAccountTitle=""; 
					  String sAddress="";
					  
					  ServletContext context=request.getSession().getServletContext(); 
					  String sImageDirectory = "D:\\QR\\";
					  String sImagePath=context.getRealPath(sImageDirectory);
					  String sDirectory ="/pages/merchantadminpanel/qrgeneration/"; 
					  String sSubReportPath=context.getRealPath(sDirectory);
					  System.out.println("dire "+sImageDirectory); 
					  HashMap hParameters=new HashMap(); hParameters.put("sQrImage",new File(sImageDirectory +"QrImage.png")); 
					  hParameters.put("sUserInfo",new File(sImageDirectory +"UserInfo.png"));
					  hParameters.put("sAccountTitle",oBanglaQRGenerationForm.getAccountTitle());
					  hParameters.put("sAddress",oBanglaQRGenerationForm.getAddress());
					  hParameters.put("sUploadFile",new File(sImageDirectory,oBanglaQRGenerationForm.getAccountNo()+".png"));
					  hParameters.put("sBanglaImage",new File(sImageDirectory +	"BanglaImage.png"));
					  hParameters.put("sBankImage",new File(sImageDirectory +"BankImage.png"));
					  hParameters.put("sMerchantImage",new File(sImageDirectory+ "MerchantImage.png"));
					  hParameters.put("sSubReportPath",sSubReportPath+"/"); 
					  String sFileName = sDirectory + "BanglaQR.jasper";
					  System.out.println("oBanglaQRGenerationForm.getAccountTitle()=====>" +
					  oBanglaQRGenerationForm.getAccountTitle());
					  System.out.println("oBanglaQRGenerationForm.getAccountNo()=====>" +
					  oBanglaQRGenerationForm.getAccountNo());
					  System.out.println("oBanglaQRGenerationForm.getAddress()=====>" +
					  oBanglaQRGenerationForm.getAddress());
					 
				
					/*
					 * String contentType = null; contentType = "application/OCTET-STREAM"; byte[]
					 * file = getFileOnServer(sImageDirectory+ "UserInfo.png");
					 * response.setHeader("Content-disposition", "attachment;filename=" +
					 * sFileName); response.setHeader("charset", "iso-8859-1");
					 * response.setContentType(contentType); response.setContentLength(file.length);
					 * response.setStatus(HttpServletResponse.SC_OK);
					 * 
					 * OutputStream outputStream = null; try { outputStream =
					 * response.getOutputStream(); outputStream.write(file, 0, file.length);
					 * outputStream.flush(); outputStream.close(); response.flushBuffer(); } catch
					 * (IOException e) { throw new RuntimeException(e); }
					 */
				
					
					  ReportManager oReportManager = new ReportManager();
					  oReportManager.viewReport(request, response, hParameters, sFileName);
					  sSuccess = sSuccessAction;
					 
				} 
		}
				else if (oBanglaQRGenerationBO.getErrorCode().equals("1")) {
					
					String	BanglaQRGenerationMessageBO =oBanglaQRGenerationBO.getErrorMessage();							
					session.setAttribute("oBanglaQRGenerationMessageBO",BanglaQRGenerationMessageBO);
					
					sSuccess = sFailureAction;
				} 
				else if (oBanglaQRGenerationBO.getErrorCode().equals("2")) {
					clearSession(session);
					sSuccess = sSessionExpireAction;
				} 
				else if (oBanglaQRGenerationBO.getErrorCode().equals("3")) {
					clearSession(session);
					sSuccess = sSessionMyBankMenuAction;
				} 
				else {
					clearSession(session);
					sSuccess = sFatalErrorAction;
				}									
			} 	

else if (sActionPath.equals("/cancelBanglaQRGeneration")) {
		session.setAttribute("oBanglaQRGenerationMessageBO",null);
		
		oBanglaQRGenerationBO =(BanglaQRGenerationBO) oBanglaQRGenerationDAO.getMenuCheckPro(gsUserID,
				gsSessionID,
				gsCompanyID,
				gsBranchID,
				request.getRemoteAddr(),
				sBranchActionPathName);
	

	if (oBanglaQRGenerationBO.getErrorCode().equals("0")) {
		//clearSession(session);
		String	BanglaQRGenerationMessageBO =oBanglaQRGenerationBO.getErrorMessage();				
		session.setAttribute("oBanglaQRGenerationMessageBO",BanglaQRGenerationMessageBO);
		sSuccess = sSuccessAction;
	} 
	else if (oBanglaQRGenerationBO.getErrorCode().equals("1")) {
		String	BanglaQRGenerationMessageBO =oBanglaQRGenerationBO.getErrorMessage();							
		session.setAttribute("oBanglaQRGenerationMessageBO",BanglaQRGenerationMessageBO);
		sSuccess = sFailureAction;
	} 
	else if (oBanglaQRGenerationBO.getErrorCode().equals("2")) {
		clearSession(session);
		sSuccess = sSessionExpireAction;
	} 
	else if (oBanglaQRGenerationBO.getErrorCode().equals("3")) {
		clearSession(session);
		sSuccess = sSessionMyBankMenuAction;
	} 
	else {
		clearSession(session);
		sSuccess = sFatalErrorAction;
	}
}

return mapping.findForward(sSuccess);
}

	
	
	 public enum TagLevel {
	        PAYLOAD,
	        POINT_OF_INITIATION_METHOD,
	        MERCHANT_ACCOUNT_NO,
	        MERCHANT_CATAGORY_CODE,
	        TRANSACTION_CURRENCY,
	        TRANSACTION_AMOUNT,
	        COUNTRY_CODE,
	        MERCHANT_NAME,
	        MERCHANT_CITY,
	        POSTAL_CODE,
	        CRC
	        
	    }
	    
	     public  String getsr(TagLevel level) {
	        String rValueTag = "";
	        switch (level) {
	            case PAYLOAD:
	                rValueTag = "00";
	                break;
	            case POINT_OF_INITIATION_METHOD:
	                rValueTag = "01";
	                break;
	            case MERCHANT_ACCOUNT_NO:
	                rValueTag = "02";
	                break;
	            case MERCHANT_CATAGORY_CODE:
	                rValueTag = "52";
	                break;
	            case TRANSACTION_CURRENCY:
	                rValueTag = "53";
	                break;
	            case TRANSACTION_AMOUNT:
	                rValueTag = "54";
	                break;
	            case COUNTRY_CODE:
	                rValueTag = "58";
	                break;
	            case MERCHANT_NAME:
	                rValueTag = "59";
	                break;
	            case MERCHANT_CITY:
	                rValueTag = "60";
	                break;
	            case POSTAL_CODE:
	                rValueTag = "61";
	                break;
	            case CRC:
	                rValueTag = "63";
	                break;
	        }

	        return rValueTag;
	    }
	     public   String getLent(int  leng){
	         String len ="";

	         if(leng<10){
	             len = "0"+leng;
	         }else{
	             len = Integer.toString(leng);
	         }


	         return len;

	     }
	     
	     
	     public  void createQRCode(String qrCodeData, String filePath,String charset, HashMap<EncodeHintType, ErrorCorrectionLevel> hintMap, int qrCodeheight, int qrCodewidth)
	 			throws WriterException, IOException {
	 	
	                 BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset),
	 		BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight);
	                 
	 		MatrixToImageWriter.writeToFile(matrix, filePath.substring(filePath.lastIndexOf('.') + 1), new File(filePath));
	 	     }
	     
	     public  String readQRCode(String filePath, String charset, Map hintMap) throws FileNotFoundException, IOException, NotFoundException {
	 		
	                 BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(filePath)))));
	 		Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap);
	 		return qrCodeResult.getText();
	 	}
	     
	     
	     private byte[] getFileOnServer(String filePath) {
       	  // file to byte[], File -> Path
		 byte[] bytes =null;
		 try {
   	  File file = new File(filePath);
   	  bytes = Files.readAllBytes(file.toPath());
   	  
		 }catch(IOException ix) {
			 
			 ix.printStackTrace();
		 }
 
   	  return bytes;
}
	     
	     private void clearSession(HttpSession session) {
	 		session.setAttribute("oBranchWiseActiveUserReportMessageBO", " ");	
	 		session.setAttribute("oBranchWiseActiveUserReportCustomerDetailsListBO", null);
	 		session.setAttribute("oBranchWiseActiveUserReportListBO", null);
	 		
	 	}


}
