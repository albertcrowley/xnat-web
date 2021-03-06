/*
 * web: xnat_studyProtocol_variable.js
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

/*
 * GENERATED FILE
 * Created on Fri Feb 12 15:43:51 CST 2016
 *
 */

/**
 * @author XDAT
 *
 */

function xnat_studyProtocol_variable(){
this.xsiType="xnat:studyProtocol_variable";

	this.getSchemaElementName=function(){
		return "studyProtocol_variable";
	}

	this.getFullSchemaElementName=function(){
		return "xnat:studyProtocol_variable";
	}

	this.Id=null;


	function getId() {
		return this.Id;
	}
	this.getId=getId;


	function setId(v){
		this.Id=v;
	}
	this.setId=setId;

	this.Name=null;


	function getName() {
		return this.Name;
	}
	this.getName=getName;


	function setName(v){
		this.Name=v;
	}
	this.setName=setName;

	this.Description=null;


	function getDescription() {
		return this.Description;
	}
	this.getDescription=getDescription;


	function setDescription(v){
		this.Description=v;
	}
	this.setDescription=setDescription;

	this.XnatStudyprotocolVariableId=null;


	function getXnatStudyprotocolVariableId() {
		return this.XnatStudyprotocolVariableId;
	}
	this.getXnatStudyprotocolVariableId=getXnatStudyprotocolVariableId;


	function setXnatStudyprotocolVariableId(v){
		this.XnatStudyprotocolVariableId=v;
	}
	this.setXnatStudyprotocolVariableId=setXnatStudyprotocolVariableId;

	this.subjectvariables_variable_xnat__xnat_abstractprotocol_id_fk=null;


	this.getsubjectvariables_variable_xnat__xnat_abstractprotocol_id=function() {
		return this.subjectvariables_variable_xnat__xnat_abstractprotocol_id_fk;
	}


	this.setsubjectvariables_variable_xnat__xnat_abstractprotocol_id=function(v){
		this.subjectvariables_variable_xnat__xnat_abstractprotocol_id_fk=v;
	}


	this.getProperty=function(xmlPath){
			if(xmlPath.startsWith(this.getFullSchemaElementName())){
				xmlPath=xmlPath.substring(this.getFullSchemaElementName().length + 1);
			}
			if(xmlPath=="ID"){
				return this.Id ;
			} else 
			if(xmlPath=="name"){
				return this.Name ;
			} else 
			if(xmlPath=="description"){
				return this.Description ;
			} else 
			if(xmlPath=="meta"){
				return this.Meta ;
			} else 
			if(xmlPath=="xnat_studyProtocol_variable_id"){
				return this.XnatStudyprotocolVariableId ;
			} else 
			if(xmlPath=="subjectvariables_variable_xnat__xnat_abstractprotocol_id"){
				return this.subjectvariables_variable_xnat__xnat_abstractprotocol_id_fk ;
			} else 
			{
				return null;
			}
	}


	this.setProperty=function(xmlPath,value){
			if(xmlPath.startsWith(this.getFullSchemaElementName())){
				xmlPath=xmlPath.substring(this.getFullSchemaElementName().length + 1);
			}
			if(xmlPath=="ID"){
				this.Id=value;
			} else 
			if(xmlPath=="name"){
				this.Name=value;
			} else 
			if(xmlPath=="description"){
				this.Description=value;
			} else 
			if(xmlPath=="meta"){
				this.Meta=value;
			} else 
			if(xmlPath=="xnat_studyProtocol_variable_id"){
				this.XnatStudyprotocolVariableId=value;
			} else 
			if(xmlPath=="subjectvariables_variable_xnat__xnat_abstractprotocol_id"){
				this.subjectvariables_variable_xnat__xnat_abstractprotocol_id_fk=value;
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
		if (xmlPath=="ID"){
			return "field_data";
		}else if (xmlPath=="name"){
			return "field_data";
		}else if (xmlPath=="description"){
			return "field_data";
		}
		else{
		}
	}


	this.toXML=function(xmlTxt,preventComments){
		xmlTxt+="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		xmlTxt+="\n<xnat:studyProtocol_variable";
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
		xmlTxt+="\n</xnat:studyProtocol_variable>";
		return xmlTxt;
	}


	this.getXMLComments=function(preventComments){
		var str ="";
		if((preventComments==undefined || !preventComments) && this.hasXMLComments()){
		str += "<!--hidden_fields[";
		var hiddenCount = 0;
			if(this.XnatStudyprotocolVariableId!=null){
				if(hiddenCount++>0)str+=",";
				str+="xnat_studyProtocol_variable_id=\"" + this.XnatStudyprotocolVariableId + "\"";
			}
			if(this.subjectvariables_variable_xnat__xnat_abstractprotocol_id_fk!=null){
				if(hiddenCount++>0)str+=",";
				str+="subjectvariables_variable_xnat__xnat_abstractprotocol_id=\"" + this.subjectvariables_variable_xnat__xnat_abstractprotocol_id_fk + "\"";
			}
		str +="]-->";
		}
		return str;
	}


	this.getXMLAtts=function(){
		var attTxt = "";
		if (this.Id!=null)
			attTxt+=" ID=\"" +this.Id +"\"";
		//NOT REQUIRED FIELD

		if (this.Name!=null)
			attTxt+=" name=\"" +this.Name +"\"";
		//NOT REQUIRED FIELD

		if (this.Description!=null)
			attTxt+=" description=\"" +this.Description +"\"";
		//NOT REQUIRED FIELD

		return attTxt;
	}


	this.getXMLBody=function(preventComments){
		var xmlTxt=this.getXMLComments(preventComments);
		return xmlTxt;
	}


	this.hasXMLComments=function(){
			if (this.XnatStudyprotocolVariableId!=null) return true;
			if (this.subjectvariables_variable_xnat__xnat_abstractprotocol_id_fk!=null) return true;
			return false;
	}


	this.hasXMLBodyContent=function(){
		if(this.hasXMLComments())return true;
		return false;
	}
}
