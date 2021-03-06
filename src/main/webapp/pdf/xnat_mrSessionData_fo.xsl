<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ xnat-web: src/main/webapp/pdf/xnat_mrSessionData_fo.xsl
  ~ XNAT http://www.xnat.org
  ~ Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
  ~ All Rights Reserved
  ~
  ~ Released under the Simplified BSD.
  -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<xsl:attribute-set name="title">
		<xsl:attribute name="font-family">Times Roman, Helvetica, serif</xsl:attribute>
		<xsl:attribute name="font-style">normal</xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="font-size">20pt</xsl:attribute>
		<xsl:attribute name="space-after">16pt</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="page-header">
		<xsl:attribute name="font-family">Times Roman, Helvetica, serif</xsl:attribute>
		<xsl:attribute name="font-style">normal</xsl:attribute>
		<xsl:attribute name="font-weight">normal</xsl:attribute>
		<xsl:attribute name="font-size">12pt</xsl:attribute>
		<xsl:attribute name="space-before">12pt</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="section-header">
		<xsl:attribute name="font-family">Times Roman, Helvetica, serif</xsl:attribute>
		<xsl:attribute name="font-style">normal</xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="font-size">18pt</xsl:attribute>
		<xsl:attribute name="space-after">16pt</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="text-header">
		<xsl:attribute name="font-family">Times Roman, Helvetica, serif</xsl:attribute>
		<xsl:attribute name="font-style">normal</xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="font-size">14pt</xsl:attribute>
		<xsl:attribute name="space-after">16pt</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="text-footer">
		<xsl:attribute name="font-family">Times Roman, Helvetica, serif</xsl:attribute>
		<xsl:attribute name="font-style">normal</xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="font-size">10pt</xsl:attribute>
		<xsl:attribute name="space-after">16pt</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="table-text">
		<xsl:attribute name="font-family">Times Roman, Helvetica, serif</xsl:attribute>
		<xsl:attribute name="font-style">normal</xsl:attribute>
		<xsl:attribute name="font-weight">normal</xsl:attribute>
		<xsl:attribute name="font-size">12pt</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="borders">
		<xsl:attribute name="border-before-width">0.5pt</xsl:attribute>
		<xsl:attribute name="border-after-width">0.5pt</xsl:attribute>
		<xsl:attribute name="border-start-width">0.5pt</xsl:attribute>
		<xsl:attribute name="border-end-width">0.5pt</xsl:attribute>
		<xsl:attribute name="padding-before">1pt</xsl:attribute>
		<xsl:attribute name="padding-after">1pt</xsl:attribute>
		<xsl:attribute name="padding-start">1pt</xsl:attribute>
		<xsl:attribute name="padding-end">1pt</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="solid">
		<xsl:attribute name="border-before-style">solid</xsl:attribute>
		<xsl:attribute name="border-after-style">solid</xsl:attribute>
		<xsl:attribute name="border-start-style">solid</xsl:attribute>
		<xsl:attribute name="border-end-style">solid</xsl:attribute>
	</xsl:attribute-set>
	<xsl:template match="/">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<fo:simple-page-master master-name="simple" page-height="29.7cm" page-width="21cm" margin-left="1.8cm" margin-right="2.0cm" margin-bottom="1cm" margin-top="1cm">
					<fo:region-start extent="0 cm"/>
					<fo:region-before extent="4.9cm"/>
					<fo:region-body margin-top="4.9cm" margin-bottom="2cm" margin-left="0cm"/>
					<fo:region-after extent="1cm"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="simple">
				<fo:static-content flow-name="xsl-region-after">
					<fo:table xsl:use-attribute-sets="table-text">
						<fo:table-column column-width="100%"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block text-align="right" font-size="10pt"><fo:inline>Document Production Date: <xsl:value-of select="data/attribute::current_date"/></fo:inline></fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:static-content>
				<fo:static-content flow-name="xsl-region-before">
					<fo:table xsl:use-attribute-sets="table-text">
						<fo:table-column column-width="60%"/>
						<fo:table-column column-width="40%"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:external-graphic>
										<xsl:attribute name="src">http://localhost:7080/cnda_xnat/images/logo.jpg</xsl:attribute>
									</fo:external-graphic>
								</fo:table-cell>
								<fo:table-cell>
									<fo:table space-before="10pt">
										<fo:table-column column-width="100%"/>
										<fo:table-body>
											<fo:table-row height="50%">
												<fo:table-cell display-align="before">
													<fo:block text-align="right">
														<fo:inline>Session: <xsl:value-of select="data/mrSessionData/attribute::ID"/>&#xa0;&#xa0;&#xa0; ID: <xsl:choose>
																<xsl:when test="count(data/subjectData/metadata/map)=1">
																	<xsl:value-of select="data/subjectData/metadata/map"/>
																</xsl:when>
																<xsl:otherwise>
																	<xsl:value-of select="data/subjectData/metadata/lab_id"/>
																</xsl:otherwise>
															</xsl:choose></fo:inline>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row height="50%">
												<fo:table-cell display-align="after">
													<fo:block text-align="right" display-align="after">
														<fo:inline>STRUCTURAL MR SESSION REPORT</fo:inline>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					<fo:block>
						<fo:leader leader-pattern="rule" leader-length="100%"/>
					</fo:block>
					<fo:table xsl:use-attribute-sets="table-text" space-before="2pt">
						<fo:table-column column-width="25%"/>
						<fo:table-column column-width="25%"/>
						<fo:table-column column-width="25%"/>
						<fo:table-column column-width="25%"/>
						<fo:table-body line-height="13pt">
							<fo:table-row>
								<fo:table-cell>
									<fo:block>
										<fo:inline font-weight="bold">Session:</fo:inline>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>
										<fo:inline>
											<xsl:value-of select="data/mrSessionData/attribute::ID"/>
										</fo:inline>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>
										<fo:inline font-weight="bold">Type:</fo:inline>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>
										<fo:inline>
											<xsl:value-of select="data/mrSessionData/attribute::session_type"/>
										</fo:inline>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
									<fo:block>
										<fo:inline font-weight="bold">Investigator:</fo:inline>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>
										<fo:inline>
											<xsl:value-of select="data/mrSessionData/investigator/firstname"/>&#xa0;<xsl:value-of select="data/mrSessionData/investigator/lastname"/>
										</fo:inline>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>
										<fo:inline font-weight="bold">Participant:</fo:inline>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>
										<fo:inline>
											<xsl:choose>
																<xsl:when test="count(data/subjectData/metadata/map)=1">
																	<xsl:value-of select="data/subjectData/metadata/map"/>
																</xsl:when>
																<xsl:otherwise>
																	<xsl:value-of select="data/subjectData/metadata/lab_id"/>
																</xsl:otherwise>
															</xsl:choose>
										</fo:inline>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
									<fo:block>
										<fo:inline font-weight="bold">Date:</fo:inline>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>
										<fo:inline>
											<xsl:value-of select="data/mrSessionData/date"/>
										</fo:inline>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>
										<fo:inline font-weight="bold"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>
										<fo:inline font-weight="bold"/>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					<fo:block space-before="0pt">
						<fo:leader leader-pattern="rule" leader-length="100%" space-before="0pt"/>
					</fo:block>
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body">
					<fo:block>
						<xsl:apply-templates select="data/mrSessionData"/>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	<xsl:template match="data/mrSessionData">
		<fo:block>
			<xsl:call-template name="DetailsTable">
				<xsl:with-param name="expt" select="."/>
			</xsl:call-template>
			<xsl:call-template name="AssessmentSummary">
				<xsl:with-param name="expt" select="."/>
			</xsl:call-template>
		</fo:block>
	</xsl:template>
	<xsl:template name="AssessmentSummary">
		<xsl:param name="expt"/>
		<fo:block space-before="18pt" xsl:use-attribute-sets="table-text">
			<fo:table>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="180pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="180pt"/>
				<fo:table-body line-height="18pt">
					<fo:table-row>
						<fo:table-cell>
							<fo:block>
								<fo:inline font-weight="bold">eTIV:</fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block>
								<xsl:if test="count($expt/assessors/assessor/eICV)!=0">
									<fo:inline><xsl:value-of select="$expt/assessors/assessor/eICV"/> cm³ (ASF:<xsl:value-of select="$expt/assessors/assessor/scalingFactor"/>, ETA:<xsl:value-of select="$expt/assessors/assessor/eta"/>)</fo:inline>
</xsl:if>
								<xsl:if test="count($expt/assessors/assessor/eICV)=0">
									<fo:inline>N/A</fo:inline>
								</xsl:if>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block>
								<fo:inline font-weight="bold">nWBV:</fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block>
								<xsl:if test="count($expt/assessors/assessor/attribute::brainPercent)!=0">
									<xsl:value-of select="$expt/assessors/assessor/attribute::brainPercent"/>
								</xsl:if>
								<xsl:if test="count($expt/assessors/assessor/attribute::brainPercent)=0">
									<fo:inline>N/A</fo:inline>
								</xsl:if>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
			<fo:table space-before="12pt">
				<fo:table-column column-width="500pt"/>
				<fo:table-body line-height="18pt">
					<fo:table-row>
						<fo:table-cell>
							<fo:block><fo:inline font-weight="bold">Additional experiments as of <xsl:value-of select="//data/attribute::current_date"/></fo:inline></fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
							<fo:block><fo:inline>Sessions:
							<xsl:for-each select="//data/subjectData/experiments/experiment[@xsi:type='xnat:mrSessionData']">
								<xsl:if test="attribute::ID!=$expt/attribute::ID">
									&#xa0;&#xa0;<xsl:value-of select="attribute::ID"/>
								</xsl:if>
							</xsl:for-each>
							</fo:inline>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
							<fo:block><fo:inline>Assessments: <xsl:value-of select="count($expt/assessors/assessor[@xsi:type='cnda:modifiedScheltensData'])"/> White Matter, <xsl:value-of select="count($expt/assessors/assessor[@xsi:type='cnda:manualVolumetryData'])"/> Volumetry </fo:inline></fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</xsl:template>
	<xsl:template name="DetailsTable">
		<xsl:param name="expt"/>
		<fo:block space-after="0pt" xsl:use-attribute-sets="table-text">
			<fo:table>
				<fo:table-column column-width="270pt"/>
				<fo:table-column column-width="300pt"/>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell>
							<fo:table xsl:use-attribute-sets="borders">
								<fo:table-column column-width="80pt"/>
								<fo:table-column column-width="190pt"/>
								<fo:table-body line-height="18pt">
									<fo:table-row>
										<fo:table-cell number-columns-spanned="2">
											<fo:block>
												<fo:inline font-weight="bold" text-decoration="underline">Session Details </fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block>
												<fo:inline font-weight="bold">Operator: </fo:inline>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block>
												<xsl:value-of select="$expt/operator"/>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block>
												<fo:inline font-weight="bold">Cohort: </fo:inline>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block>
												<xsl:value-of select="//data/subjectData/metadata/cohort"/>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block>
												<fo:inline font-weight="bold">Scanner: </fo:inline>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block>
												<xsl:value-of select="$expt/scanner"/>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block>
												<fo:inline font-weight="bold">Marker: </fo:inline>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block>
												<xsl:value-of select="$expt/marker"/>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block>
												<fo:inline font-weight="bold">Stabilization: </fo:inline>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block>
												<xsl:value-of select="$expt/stabilization"/>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:table-cell>
						<fo:table-cell>
							<fo:table>
								<fo:table-column column-width="80pt"/>
								<fo:table-column column-width="160pt"/>
								<fo:table-body line-height="18pt">'
									<fo:table-row>
										<fo:table-cell number-columns-spanned="2">
											<fo:block>
												<fo:inline font-weight="bold" text-decoration="underline">Participant Details </fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block>
												<fo:inline font-weight="bold">MAP #: </fo:inline>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block>
												<xsl:value-of select="//data/subjectData/metadata/map"/>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block>
												<fo:inline font-weight="bold">Lab Id: </fo:inline>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block>
												<xsl:value-of select="//data/subjectData/metadata/lab_id"/>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block>
												<fo:inline font-weight="bold">Gender: </fo:inline>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block>
												<xsl:value-of select="//data/subjectData/demographics/gender"/>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block>
												<fo:inline font-weight="bold">Handedness: </fo:inline>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block>
												<xsl:value-of select="//data/subjectData/demographics/handedness"/>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell>
											<fo:block>
												<fo:inline font-weight="bold">Age (at Scan): </fo:inline>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block>
												<xsl:value-of select="//data/subjectData/demographics/age"/>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
			<fo:table>
				<fo:table-column column-width="80pt"/>
				<fo:table-column column-width="420pt"/>
				<fo:table-body line-height="18pt">
					<fo:table-row>
						<fo:table-cell>
							<fo:block>
								<fo:inline font-weight="bold">Scans: </fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block>
								<fo:inline>
									<xsl:if test="count($expt/scans/scan[attribute::type='MPRAGE'])!=0">
										<xsl:value-of select="count($expt/scans/scan[attribute::type='MPRAGE'])"/>&#xa0;MPRAGE&#xa0;&#xa0;
									</xsl:if>
									<xsl:if test="count($expt/scans/scan[attribute::type='DTI'])!=0">
										<xsl:value-of select="count($expt/scans/scan[attribute::type='DTI'])"/>&#xa0;DTI&#xa0;&#xa0;
									</xsl:if>
									<xsl:if test="count($expt/scans/scan[attribute::type='TSE'])!=0">
										<xsl:value-of select="count($expt/scans/scan[attribute::type='TSE'])"/>&#xa0;TSE&#xa0;&#xa0;
									</xsl:if>
									<xsl:if test="count($expt/scans/scan[attribute::type='FLASH3D'])!=0">
										<xsl:value-of select="count($expt/scans/scan[attribute::type='FLASH3D'])"/>&#xa0;FLASH3D&#xa0;&#xa0;
									</xsl:if>
								</fo:inline>
								
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
							<fo:block>
								<fo:inline font-weight="bold">Notes: </fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block>
								<xsl:value-of select="$expt/note"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
			<fo:table space-before="24pt" text-align="center">
				<fo:table-column column-width="100%"/>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell>
							<fo:block>
								<fo:inline>Quality Control Images</fo:inline>
							
			<fo:table space-before="10pt" text-align="center">
				<fo:table-column column-width="250pt"/>
				<fo:table-column column-width="250pt"/>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell>
							<fo:block>
								<fo:inline>MPR IMAGE</fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block>
								<fo:inline>TSE IMAGE</fo:inline>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
							<fo:block>
								<fo:external-graphic>
									<xsl:attribute name="src">file:////data/iac14/space11/CNDAWEB/<xsl:value-of select="$expt/attribute::ID"/>/WEB/<xsl:value-of select="$expt/attribute::ID"/>_mpr_qc.gif</xsl:attribute>
								</fo:external-graphic>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block>
								<fo:external-graphic>
									<xsl:attribute name="src">file:////data/iac14/space11/CNDAWEB/<xsl:value-of select="$expt/attribute::ID"/>/WEB/<xsl:value-of select="$expt/attribute::ID"/>_tse_qc.gif</xsl:attribute>
								</fo:external-graphic>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</xsl:template>
	<xsl:template name="RadReadReport">
		<xsl:param name="expt"/>
		<fo:block xsl:use-attribute-sets="text-header" keep-with-next.within-page="always">
			<xsl:attribute name="space-before">18pt</xsl:attribute>
			<xsl:attribute name="space-after">4pt</xsl:attribute>
			<fo:inline font-weight="bold">Radiologic Evaluation</fo:inline>
		</fo:block>
		<fo:block space-after="0pt" xsl:use-attribute-sets="table-text">
			<fo:table>
				<fo:table-column column-width="80pt"/>
				<fo:table-column column-width="190pt"/>
				<fo:table-column column-width="80pt"/>
				<fo:table-column column-width="160pt"/>
				<fo:table-body line-height="18pt">
					<fo:table-row>
						<fo:table-cell>
							<fo:block>
								<fo:inline font-weight="bold">Reader:</fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block>
								<xsl:value-of select="$expt/RadRead/reader"/>
								<xsl:if test="count($expt/RadRead)=0">
									<fo:inline>N/A</fo:inline>
								</xsl:if>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block>
								<fo:inline font-weight="bold">Normal: </fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block>
								<xsl:value-of select="$expt/RadRead/finding/@normal_status"/>
								<xsl:if test="count($expt/RadRead)=0">
									<fo:inline>N/A</fo:inline>
								</xsl:if>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
			<fo:table>
				<fo:table-column column-width="80pt"/>
				<fo:table-column column-width="520pt"/>
				<fo:table-body line-height="18pt">
					<fo:table-row>
						<fo:table-cell>
							<fo:block>
								<fo:inline font-weight="bold">Technique: </fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block>
								<xsl:value-of select="$expt/RadRead/technique"/>
								<xsl:if test="count($expt/RadRead)=0">
									<fo:inline>N/A</fo:inline>
								</xsl:if>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
							<fo:block>
								<fo:inline font-weight="bold">Finding: </fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block>
								<xsl:value-of select="$expt/RadRead/finding"/>
								<xsl:if test="count($expt/RadRead)=0">
									<fo:inline>N/A</fo:inline>
								</xsl:if>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
							<fo:block>
								<fo:inline font-weight="bold">Diagnosis: </fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block>
								<xsl:value-of select="$expt/RadRead/diagnosis"/>
								<xsl:if test="count($expt/RadRead)=0">
									<fo:inline>N/A</fo:inline>
								</xsl:if>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
		<xsl:if test="count($expt/RadRead)>1">
			<fo:block xsl:use-attribute-sets="text-footer">
				<xsl:attribute name="space-before">4pt</xsl:attribute>
				<fo:inline font-weight="bold">* Multiple Reads Available</fo:inline>
			</fo:block>
		</xsl:if>
	</xsl:template>
	<xsl:template name="CondensedASF">
		<xsl:param name="expt"/>
		<fo:block space-after="0pt" xsl:use-attribute-sets="table-text">
			<fo:table>
				<fo:table-column column-width="80pt"/>
				<fo:table-column column-width="190pt"/>
				<fo:table-column column-width="80pt"/>
				<fo:table-column column-width="160pt"/>
				<fo:table-body line-height="18pt">
					<fo:table-row>
						<fo:table-cell>
							<fo:block>
								<fo:inline font-weight="bold">eICV:</fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block>
								<xsl:if test="count($expt/AtlasScalingFactor)!=0">
									<xsl:value-of select="$expt/AtlasScalingFactor/eICV"/> (asf:<xsl:value-of select="$expt/AtlasScalingFactor/ScalingFactor"/>, eta:<xsl:value-of select="$expt/AtlasScalingFactor/Eta"/>)</xsl:if>
								<xsl:if test="count($expt/AtlasScalingFactor)=0">
									<fo:inline>N/A</fo:inline>
								</xsl:if>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block>
								<fo:inline font-weight="bold">CSF Ratio: </fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block>
								<xsl:if test="count($expt/AtrNil)!=0">
									<xsl:value-of select="$expt/AtrNil/csf_ratio/@value"/> (<xsl:value-of select="$expt/AtrNil/csf_ratio/csf"/>/<xsl:value-of select="$expt/AtrNil/csf_ratio/total"/>)</xsl:if>
								<xsl:if test="count($expt/AtrNil)=0">
									<fo:inline>N/A</fo:inline>
								</xsl:if>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
			<fo:table space-before="6pt">
				<fo:table-column column-width="120pt"/>
				<fo:table-column column-width="400pt"/>
				<fo:table-body line-height="18pt">
					<fo:table-row>
						<fo:table-cell>
							<fo:block>
								<fo:inline font-weight="bold">WM Scheltens Score:</fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block>
								<xsl:if test="count($expt/WmModScheltens)!=0">
									<xsl:value-of select="$expt/WmModScheltens/totalScheltens"/>
								</xsl:if>
								<xsl:if test="count($expt/WmModScheltens)=0">
									<fo:inline>N/A</fo:inline>
								</xsl:if>
								<xsl:if test="count($expt/WmModScheltens)>1">
									<fo:block xsl:use-attribute-sets="text-footer">
										<xsl:attribute name="space-before">4pt</xsl:attribute>
										<fo:inline font-weight="bold">* Multiple Reads Available</fo:inline>
									</fo:block>
								</xsl:if>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</xsl:template>
	<xsl:template name="SummaryTable">
		<xsl:param name="expt"/>
		<fo:block xsl:use-attribute-sets="section-header" keep-with-next.within-page="always">
			<xsl:attribute name="space-after">0pt</xsl:attribute>
			<fo:inline font-weight="bold">Scan Summary</fo:inline>
		</fo:block>
		<fo:block space-before="18pt" space-after="0pt" xsl:use-attribute-sets="table-text">
			<fo:table>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="200pt"/>
				<fo:table-header>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">
								Scan Number
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">Type</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">Quality</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">Note</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body>
					<xsl:for-each select="$expt/MrScan">
						<fo:table-row>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="scan_number"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="protocol"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="quality"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="note"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:for-each>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</xsl:template>
	<xsl:template name="AssessmentTables">
		<xsl:param name="expt"/>
		<fo:block xsl:use-attribute-sets="title" keep-with-next.within-page="always">
			<xsl:attribute name="text-align">center</xsl:attribute>
			<xsl:attribute name="space-after">0pt</xsl:attribute>
			<fo:inline font-weight="bold">Assessments</fo:inline>
		</fo:block>
		<xsl:call-template name="VolumetryTable">
			<xsl:with-param name="expt" select="$expt"/>
		</xsl:call-template>
		<xsl:call-template name="WMTable">
			<xsl:with-param name="expt" select="$expt"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template name="ASFTable">
		<xsl:param name="expt"/>
		<fo:block space-before="0pt" xsl:use-attribute-sets="section-header" keep-with-next.within-page="always">
			<xsl:attribute name="space-after">0pt</xsl:attribute>
			<fo:inline font-weight="bold">Atlas Scaling Factor</fo:inline>
		</fo:block>
		<fo:block space-before="18pt" space-after="0pt" xsl:use-attribute-sets="table-text">
			<fo:table>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="90pt"/>
				<fo:table-header>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">
								Date
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">PI</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">Scaling Factor</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">ETA</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">EICV</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body>
					<xsl:for-each select="$expt/AtlasScalingFactor">
						<fo:table-row>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="substring-before(Experiment/expt-date,'T')"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="Experiment/investigator/lastname"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="ScalingFactor"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="Eta"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="eICV"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:for-each>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</xsl:template>
	<xsl:template name="AtrophyTable">
		<xsl:param name="expt"/>
		<fo:block space-before="18pt" xsl:use-attribute-sets="section-header" keep-with-next.within-page="always">
			<xsl:attribute name="space-after">0pt</xsl:attribute>
			<fo:inline font-weight="bold">Atrophy</fo:inline>
		</fo:block>
		<fo:block space-before="18pt" space-after="0pt" xsl:use-attribute-sets="table-text">
			<fo:table>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="120pt"/>
				<fo:table-header>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">
								Date
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">PI</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">CSF</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">Total</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">Ratio</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body>
					<xsl:for-each select="$expt/AtrNil">
						<fo:table-row>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="substring-before(Experiment/expt-date,'T')"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="Experiment/investigator/lastname"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="csf_ratio/csf"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="csf_ratio/total"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="csf_ratio/@value"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:for-each>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</xsl:template>
	<xsl:template name="RadReadTable">
		<xsl:param name="expt"/>
		<fo:block space-before="18pt" xsl:use-attribute-sets="section-header" keep-with-next.within-page="always">
			<xsl:attribute name="space-after">0pt</xsl:attribute>
			<fo:inline font-weight="bold">Radiology Reads</fo:inline>
		</fo:block>
		<fo:block space-before="18pt" space-after="0pt" xsl:use-attribute-sets="table-text">
			<fo:table>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="120pt"/>
				<fo:table-header>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">
								Date
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">PI</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">Reader</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">Modality</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">Technique</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body>
					<xsl:for-each select="$expt/RadRead">
						<fo:table-row>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="substring-before(Experiment/expt-date,'T')"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="Experiment/investigator/lastname"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="reader"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="modality"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="technique"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:for-each>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</xsl:template>
	<xsl:template name="VolumetryTable">
		<xsl:param name="expt"/>
		<fo:block space-before="18pt" xsl:use-attribute-sets="section-header" keep-with-next.within-page="always">
			<xsl:attribute name="space-after">0pt</xsl:attribute>
			<fo:inline font-weight="bold">Volumetry</fo:inline>
		</fo:block>
		<fo:block space-before="18pt" space-after="0pt" xsl:use-attribute-sets="table-text">
			<fo:table>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="120pt"/>
				<fo:table-header>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">
								Date
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">PI</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">Region</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">Volume</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">Data Type</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body>
					<xsl:for-each select="$expt/Volumetry">
						<fo:table-row>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="substring-before(Experiment/expt-date,'T')"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="Experiment/investigator/lastname"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="anatomic_region"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="total_volume"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="datatype"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:for-each>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</xsl:template>
	<xsl:template name="WMTable">
		<xsl:param name="expt"/>
		<fo:block space-before="18pt" xsl:use-attribute-sets="section-header" keep-with-next.within-page="always">
			<xsl:attribute name="space-after">0pt</xsl:attribute>
			<fo:inline font-weight="bold">White Matter Assessments</fo:inline>
		</fo:block>
		<fo:block space-before="18pt" space-after="0pt" xsl:use-attribute-sets="table-text">
			<fo:table>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="45pt"/>
				<fo:table-column column-width="45pt"/>
				<fo:table-column column-width="45pt"/>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="45pt"/>
				<fo:table-header>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">
								Date
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">PI</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">DWM</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">Pv</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">Bg</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">DWM Burden</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="borders solid" background-color="lightgrey">
							<fo:block text-decoration="underline" font-weight="bold">Total</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body>
					<xsl:for-each select="$expt/WmModScheltens">
						<fo:table-row>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="substring-before(expt-data/expt-date,'T')"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="expt-data/investigator/lastname"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="dwmScheltens"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="pvScheltens"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="bgScheltens"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="dwmBurden"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets="borders solid">
								<fo:block>
									<xsl:value-of select="totalScheltens"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:for-each>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</xsl:template>
</xsl:stylesheet>

