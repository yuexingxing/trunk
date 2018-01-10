package com.zhiduan.crowdclient.data;

import java.io.Serializable;

/**  
 * <pre>
 * Description
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-5-28 上午10:42:21  
 * </pre>
 */

public class SchoolInfo implements Serializable{
    
//	 "COLLEGE_GCODE": "U3",//学校G_GCODE
//     "COLLEGE_NAME": "东南大学",//学校名称
//     "COLLEGE_CODE": "00002",//学校编号
//     "SHORT_NAME": "东南大",//学校简称
//     "PRONAME": "江苏省",//省份
//     "CITYNAME": "南京市",//城市
//     "COUNTYNAME": "栖霞区"//区县

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String COLLEGE_GCODE;
	public String COLLEGE_NAME;
	public String COLLEGE_CODE;
	public String SHORT_NAME;
	public String PRONAME;
	public String CITYNAME;
	public String COUNTYNAME;
	public boolean isSelect;
	public String pinYin;//校区拼音
	
	public String getCOLLEGE_GCODE() {
		return COLLEGE_GCODE;
	}
	public void setCOLLEGE_GCODE(String cOLLEGE_GCODE) {
		COLLEGE_GCODE = cOLLEGE_GCODE;
	}
	public String getCOLLEGE_NAME() {
		return COLLEGE_NAME;
	}
	public void setCOLLEGE_NAME(String cOLLEGE_NAME) {
		COLLEGE_NAME = cOLLEGE_NAME;
	}
	public String getCOLLEGE_CODE() {
		return COLLEGE_CODE;
	}
	public void setCOLLEGE_CODE(String cOLLEGE_CODE) {
		COLLEGE_CODE = cOLLEGE_CODE;
	}
	public String getSHORT_NAME() {
		return SHORT_NAME;
	}
	public void setSHORT_NAME(String sHORT_NAME) {
		SHORT_NAME = sHORT_NAME;
	}
	public String getPRONAME() {
		return PRONAME;
	}
	public void setPRONAME(String pRONAME) {
		PRONAME = pRONAME;
	}
	public String getCITYNAME() {
		return CITYNAME;
	}
	public void setCITYNAME(String cITYNAME) {
		CITYNAME = cITYNAME;
	}
	public String getCOUNTYNAME() {
		return COUNTYNAME;
	}
	public void setCOUNTYNAME(String cOUNTYNAME) {
		COUNTYNAME = cOUNTYNAME;
	}
	public boolean isSelect() {
		return isSelect;
	}
	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}
	public String getPinYin() {
		return pinYin;
	}
	public void setPinYin(String pinYin) {
		this.pinYin = pinYin;
	}
	
}
