/*
 * web: val_protocolData_scan_check_comment.js
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *  
 * Released under the Simplified BSD.
 */

/*
 * GENERATED FILE
 * Created on Fri Feb 12 15:43:50 CST 2016
 *
 */

/**
 * @author XDAT
 *
 */

function val_protocolData_scan_check_comment(){
this.xsiType="val:protocolData_scan_check_comment";

	this.getSchemaElementName=function(){
		return "protocolData_scan_check_comment";
	}

	this.getFullSchemaElementName=function(){
		return "val:protocolData_scan_check_comment";
	}

	this.Comment=null;


	function getComment() {
		return this.Comment;
	}
	this.getComment=getComment;


	function setComment(v){
		this.Comment=v;
	}
	this.setComment=setComment;

	this.Username=null;


	function getUsername() {
		return this.Username;
	}
	this.getUsername=getUsername;


	function setUsername(v){
		this.Username=v;
	}
	this.setUsername=setUsername;

	this.Datetime=null;


	function getDatetime() {
		return this.Datetime;
	}
	this.getDatetime=getDatetime;


	function setDatetime(v){
		this.Datetime=v;
	}
	this.setDatetime=setDatetime;

	this.ValProtocoldataScanCheckCommentId=null;


	function getValProtocoldataScanCheckCommentId() {
		return this.ValProtocoldataScanCheckCommentId;
	}
	this.getValProtocoldataScanCheckCommentId=getValProtocoldataScanCheckCommentId;


	function setValProtocoldataScanCheckCommentId(v){
		this.ValProtocoldataScanCheckCommentId=v;
	}
	this.setValProtocoldataScanCheckCommentId=setValProtocoldataScanCheckCommentId;

	this.comments_comment_val_protocolDa_val_protocoldata_scan_check_id_fk=null;


	this.getcomments_comment_val_protocolDa_val_protocoldata_scan_check_id=function() {
		return this.comments_comment_val_protocolDa_val_protocoldata_scan_check_id_fk;
	}


	this.setcomments_comment_val_protocolDa_val_protocoldata_scan_check_id=function(v){
		this.comments_comment_val_protocolDa_val_protocoldata_scan_check_id_fk=v;
	}


	this.getProperty=function(xmlPath){
			if(xmlPath.startsWith(this.getFullSchemaElementName())){
				xmlPath=xmlPath.substring(this.getFullSchemaElementName().length + 1);
			}
			if(xmlPath=="comment"){
				return this.Comment ;
			} else 
			if(xmlPath=="username"){
				return this.Username ;
			} else 
			if(xmlPath=="dateTime"){
				return this.Datetime ;
			} else 
			if(xmlPath=="meta"){
				return this.Meta ;
			} else 
			if(xmlPath=="val_protocolData_scan_check_comment_id"){
				return this.ValProtocoldataScanCheckCommentId ;
			} else 
			if(xmlPath=="comments_comment_val_protocolDa_val_protocoldata_scan_check_id"){
				return this.comments_comment_val_protocolDa_val_protocoldata_scan_check_id_fk ;
			} else 
			{
				return null;
			}
	}


	this.setProperty=function(xmlPath,value){
			if(xmlPath.startsWith(this.getFullSchemaElementName())){
				xmlPath=xmlPath.substring(this.getFullSchemaElementName().length + 1);
			}
			if(xmlPath=="comment"){
				this.Comment=value;
			} else 
			if(xmlPath=="username"){
				this.Username=value;
			} else 
			if(xmlPath=="dateTime"){
				this.Datetime=value;
			} else 
			if(xmlPath=="meta"){
				this.Meta=value;
			} else 
			if(xmlPath=="val_protocolData_scan_check_comment_id"){
				this.ValProtocoldataScanCheckCommentId=value;
			} else 
			if(xmlPath=="comments_comment_val_protocolDa_val_protocoldata_scan_check_id"){
				this.comments_comment_val_protocolDa_val_protocoldata_scan_check_id_fk=value;
			} else 
			{
				return null;
			}
	}

	/**
	 * Sets the value for a field via the XMLPATH.
	 * @param v Value to Set.
	 */
	this.setReferenceField=function(xmlPath,v) {
	}

	/**
	 * Gets the value for a field via the XMLPATH.
	 * @param v Value to Set.
	 */
	this.getReferenceFieldName=function(xmlPath) {
	}

	/**
	 * Returns whether or not this is a reference field
	 */
	this.getFieldType=function(xmlPath){
		if (xmlPath=="comment"){
			return "field_data";
		}else if (xmlPath=="username"){
			return "field_data";
		}else if (xmlPath=="dateTime"){
			return "field_data";
		}
		else{
		}
	}


	this.toXML=function(xmlTxt,preventComments){
		xmlTxt+="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		xmlTxt+="\n<val:protocolData_scan_check_comment";
		xmlTxt+=this.getXMLAtts();
		xmlTxt+=" xmlns:arc=\"http://nrg.wustl.edu/arc\"";
		xmlTxt+=" xmlns:cat=\"http://nrg.wustl.edu/catalog\"";
		xmlTxt+=" xmlns:pipe=\"http://nrg.wustl.edu/pipe\"";
		xmlTxt+=" xmlns:prov=\"http://www.nbirn.net/prov\"";
		xmlTxt+=" xmlns:scr=\"http://nrg.wustl.edu/scr\"";
		xmlTxt+=" xmlns:val=\"http://nrg.wustl.edu/val\"";
		xmlTxt+=" xmlns:wrk=\"http://nrg.wustl.edu/workflow\"";
		xmlTxt+=" xmlns:xdat=\"http://nrg.wustl.edu/security\"";
		xmlTxt+=" xmlns:xnat=\"http://nrg.wustl.edu/xnat\"";
		xmlTxt+=" xmlns:xnat_a=\"http://nrg.wustl.edu/xnat_assessments\"";
		xmlTxt+=" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"";
		xmlTxt+=">";
		xmlTxt+=this.getXMLBody(preventComments)
		xmlTxt+="\n</val:protocolData_scan_check_comment>";
		return xmlTxt;
	}


	this.getXMLComments=function(preventComments){
		var str ="";
		if((preventComments==undefined || !preventComments) && this.hasXMLComments()){
		str += "<!--hidden_fields[";
		var hiddenCount = 0;
			if(this.ValProtocoldataScanCheckCommentId!=null){
				if(hiddenCount++>0)str+=",";
				str+="val_protocolData_scan_check_comment_id=\"" + this.ValProtocoldataScanCheckCommentId + "\"";
			}
			if(this.comments_comment_val_protocolDa_val_protocoldata_scan_check_id_fk!=null){
				if(hiddenCount++>0)str+=",";
				str+="comments_comment_val_protocolDa_val_protocoldata_scan_check_id=\"" + this.comments_comment_val_protocolDa_val_protocoldata_scan_check_id_fk + "\"";
			}
		str +="]-->";
		}
		return str;
	}


	this.getXMLAtts=function(){
		var attTxt = "";
		if (this.Username!=null)
			attTxt+=" username=\"" +this.Username +"\"";
		//NOT REQUIRED FIELD

		if (this.Datetime!=null)
			attTxt+=" dateTime=\"" +this.Datetime +"\"";
		else attTxt+=" dateTime=\"\"";//REQUIRED FIELD

		return attTxt;
	}


	this.getXMLBody=function(preventComments){
		var xmlTxt=this.getXMLComments(preventComments);
		if (this.Comment!=null){
			xmlTxt+=this.Comment.replace(/>/g,"&gt;").replace(/</g,"&lt;");
		}
		return xmlTxt;
	}


	this.hasXMLComments=function(){
			if (this.ValProtocoldataScanCheckCommentId!=null) return true;
			if (this.comments_comment_val_protocolDa_val_protocoldata_scan_check_id_fk!=null) return true;
			return false;
	}


	this.hasXMLBodyContent=function(){
		if (this.Comment!=null) return true;
		if(this.hasXMLComments())return true;
		return false;
	}
}
