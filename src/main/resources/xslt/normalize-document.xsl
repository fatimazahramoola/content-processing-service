<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="3.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:lex="urn:lex:content:1"
		exclude-result-prefixes="#all">

	<xsl:output method="json" indent="yes"/>

	<xsl:template match="/lex:judgment">
		<xsl:sequence select="map {
			'content_id': normalize-space(lex:header/lex:content_id),
			'title': normalize-space(lex:header/lex:title),
			'court': normalize-space(lex:header/lex:court),
			'jurisdiction': normalize-space(lex:header/lex:jurisdiction),
			'decision_date': normalize-space(lex:header/lex:decision_date),
			'citations': array {
				for $citation in lex:header/lex:citations/lex:citation
				return map {
					'type': normalize-space($citation/@type),
					'value': normalize-space($citation)
				}
			},
			'parties': array {
				for $party in lex:header/lex:parties/lex:party
				return map {
					'role': normalize-space($party/@role),
					'name': normalize-space($party)
				}
			},
			'paragraphs': array {
				for $paragraph in lex:body/lex:section/lex:p
				return map {
					'id': normalize-space($paragraph/@id),
					'section': normalize-space($paragraph/../@type),
					'text': normalize-space($paragraph)
				}
			},
			'full_text': normalize-space(string-join(lex:body/lex:section/lex:p ! normalize-space(.), ' '))
		}"/>
	</xsl:template>

</xsl:stylesheet>
