/*
 * Created on May 29, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import net.sourceforge.fullsync.rules.filefilter.values.AgeValue;
import net.sourceforge.fullsync.rules.filefilter.values.DateValue;
import net.sourceforge.fullsync.rules.filefilter.values.SizeValue;
import net.sourceforge.fullsync.rules.filefilter.values.TypeValue;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;
import net.sourceforge.fullsync.rules.filefilter.values.TextValue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author Michele Aiello
 */
public class FileFilterManager {
	
	public Element serializeFileFilter(FileFilter fileFilter, Document document, String elementName) {
		Element filterElement = document.createElement(elementName);
		document.appendChild(filterElement);
		filterElement.setAttribute("matchtype", String.valueOf(fileFilter.getMatchType()));
		filterElement.setAttribute("filtertype", String.valueOf(fileFilter.getFilterType()));
		filterElement.setAttribute("appliestodir", String.valueOf(fileFilter.appliesToDirectories()));

		FileFilterRule[] rules = fileFilter.getFileFiltersRules();
		for (int i = 0; i < rules.length; i++) {
			Element ruleElement = serializeRule(rules[i], document);
			filterElement.appendChild(ruleElement);
		}
		
		return filterElement;
	}

	public Element serializeRule(FileFilterRule fileFilterRule, Document document) {
		Element ruleElement = document.createElement("FileFilterRule");
		String ruleType = getRuleType(fileFilterRule);
		
		ruleElement.setAttribute("ruletype", ruleType);
		serializeRuleAttributes(fileFilterRule, ruleElement);
		
		String desc = fileFilterRule.toString();
		Element descriptionElement = document.createElement("Description");
		Text descNode = document.createTextNode(desc);
		descriptionElement.appendChild(descNode);
		ruleElement.appendChild(descriptionElement);
		
		return ruleElement;
	}
	
	public FileFilter unserializeFileFilter(Element fileFilterElement) {
		FileFilter fileFilter = new FileFilter();
		int match_type = 0;

		try {
			match_type = Integer.parseInt(fileFilterElement.getAttribute("matchtype"));
		} catch (NumberFormatException e) {
		}
		fileFilter.setMatchType(match_type);
		
		int filter_type = 0;
		try {
			filter_type = Integer.parseInt(fileFilterElement.getAttribute("filtertype"));
		} catch (NumberFormatException e) {
		}
		fileFilter.setFilterType(filter_type);
		
		boolean applies = Boolean.valueOf(fileFilterElement.getAttribute("appliestodir")).booleanValue();
		
		NodeList ruleList = fileFilterElement.getElementsByTagName("FileFilterRule");
		int numOfRules = ruleList.getLength();
		FileFilterRule[] rules = new FileFilterRule[numOfRules];
		
		for (int i = 0; i < rules.length; i++) {
			rules[i] = unserializeFileFilterRule((Element)ruleList.item(i));
		}
		
		fileFilter.setFileFilterRules(rules);
		
		return fileFilter;
	}

	private void serializeRuleAttributes(FileFilterRule fileFilterRule, Element ruleElement) {
		if (fileFilterRule instanceof FileNameFileFilterRule) {
			FileNameFileFilterRule rule = (FileNameFileFilterRule) fileFilterRule;
			ruleElement.setAttribute("op", String.valueOf(rule.getOperator()));
			ruleElement.setAttribute("pattern", rule.getValue().toString());
		}

		if (fileFilterRule instanceof FilePathFileFilterRule) {
			FilePathFileFilterRule rule = (FilePathFileFilterRule) fileFilterRule;
			ruleElement.setAttribute("op", String.valueOf(rule.getOperator()));
			ruleElement.setAttribute("pattern", rule.getValue().toString());
		}

		if (fileFilterRule instanceof FileTypeFileFilterRule) {
			FileTypeFileFilterRule rule = (FileTypeFileFilterRule) fileFilterRule;
			ruleElement.setAttribute("op", String.valueOf(rule.getOperator()));
			ruleElement.setAttribute("type", rule.getValue().toString());
		}

		if (fileFilterRule instanceof FileSizeFileFilterRule) {
			FileSizeFileFilterRule rule = (FileSizeFileFilterRule) fileFilterRule;
			ruleElement.setAttribute("op", String.valueOf(rule.getOperator()));
			ruleElement.setAttribute("size", rule.getValue().toString());
		}
		
		if (fileFilterRule instanceof FileModificationDateFileFilterRule) {
			FileModificationDateFileFilterRule rule = (FileModificationDateFileFilterRule) fileFilterRule;
			ruleElement.setAttribute("op", String.valueOf(rule.getOperator()));
			ruleElement.setAttribute("modificationdate", rule.getValue().toString());
		}

		if (fileFilterRule instanceof FileAgeFileFilterRule) {
			FileAgeFileFilterRule rule = (FileAgeFileFilterRule) fileFilterRule;
			ruleElement.setAttribute("op", String.valueOf(rule.getOperator()));
			ruleElement.setAttribute("age", rule.getValue().toString());
		}

	}

	public FileFilterRule unserializeFileFilterRule(Element fileFilterRuleElement) {
		FileFilterRule rule = null;
		String ruleType = fileFilterRuleElement.getAttribute("ruletype");
		
		if (ruleType.equals(FileNameFileFilterRule.typeName)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute("op"));
			String pattern = fileFilterRuleElement.getAttribute("pattern");
			rule = new FileNameFileFilterRule(new TextValue(pattern), op);
		}

		if (ruleType.equals(FilePathFileFilterRule.typeName)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute("op"));
			String pattern = fileFilterRuleElement.getAttribute("pattern");
			rule = new FilePathFileFilterRule(new TextValue(pattern), op);
		}

		if (ruleType.equals(FileTypeFileFilterRule.typeName)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute("op"));
			String type = fileFilterRuleElement.getAttribute("type");
			rule = new FileTypeFileFilterRule(new TypeValue(type), op);
		}

		if (ruleType.equals(FileSizeFileFilterRule.typeName)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute("op"));
			String size = fileFilterRuleElement.getAttribute("size");
			rule = new FileSizeFileFilterRule(new SizeValue(size), op);
		}

		if (ruleType.equals(FileModificationDateFileFilterRule.typeName)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute("op"));
			String date = fileFilterRuleElement.getAttribute("modificationdate");
			rule = new FileModificationDateFileFilterRule(new DateValue(date), op);
		}

		if (ruleType.equals(FileAgeFileFilterRule.typeName)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute("op"));
			String age = fileFilterRuleElement.getAttribute("age");
			rule = new FileAgeFileFilterRule(new AgeValue(age), op);
		}

		return rule;
	}
	
	public FileFilterRule createFileFilterRule(String ruleType, int op, OperandValue value) {
		FileFilterRule rule = null;
		
		if (ruleType.equals(FileNameFileFilterRule.typeName)) {
			TextValue textValue = (TextValue)value;
			rule = new FileNameFileFilterRule(textValue, op);
		}

		if (ruleType.equals(FilePathFileFilterRule.typeName)) {
			TextValue textValue = (TextValue)value;
			rule = new FilePathFileFilterRule(textValue, op);
		}

		if (ruleType.equals(FileTypeFileFilterRule.typeName)) {
			TypeValue fileTypeValue = (TypeValue)value;
			rule = new FileTypeFileFilterRule(fileTypeValue, op);
		}

		if (ruleType.equals(FileSizeFileFilterRule.typeName)) {
			SizeValue size = (SizeValue)value;
			rule = new FileSizeFileFilterRule(size, op);
		}

		if (ruleType.equals(FileModificationDateFileFilterRule.typeName)) {
			DateValue date = (DateValue)value;
			rule = new FileModificationDateFileFilterRule(date, op);
		}

		if (ruleType.equals(FileAgeFileFilterRule.typeName)) {
			AgeValue age = (AgeValue)value;
			rule = new FileAgeFileFilterRule(age, op);
		}

		return rule;
		
	}
	
	
	public String[] getOperatorsForRuleType(String ruleType) {
		if (ruleType.equals(FileNameFileFilterRule.typeName)) {
			return FileNameFileFilterRule.getAllOperators();
		}
		
		if (ruleType.equals(FilePathFileFilterRule.typeName)) {
			return FilePathFileFilterRule.getAllOperators();
		}

		if (ruleType.equals(FileTypeFileFilterRule.typeName)) {
			return FileTypeFileFilterRule.getAllOperators();
		}

		if (ruleType.equals(FileSizeFileFilterRule.typeName)) {
			return FileSizeFileFilterRule.getAllOperators();
		}

		if (ruleType.equals(FileModificationDateFileFilterRule.typeName)) {
			return FileModificationDateFileFilterRule.getAllOperators();
		}

		if (ruleType.equals(FileAgeFileFilterRule.typeName)) {
			return FileAgeFileFilterRule.getAllOperators();
		}

		return new String[] {"N/A"};
	}


	private String getRuleType(FileFilterRule fileFilterRule) {
		if (fileFilterRule instanceof FileNameFileFilterRule) {
			return FileNameFileFilterRule.typeName;
		}

		if (fileFilterRule instanceof FilePathFileFilterRule) {
			return FilePathFileFilterRule.typeName;
		}

		if (fileFilterRule instanceof FileTypeFileFilterRule) {
			return FileTypeFileFilterRule.typeName;
		}

		if (fileFilterRule instanceof FileSizeFileFilterRule) {
			return FileSizeFileFilterRule.typeName;
		}
		
		if (fileFilterRule instanceof FileModificationDateFileFilterRule) {
			return FileModificationDateFileFilterRule.typeName;
		}
		
		if (fileFilterRule instanceof FileAgeFileFilterRule) {
			return FileAgeFileFilterRule.typeName;
		}

		return null;
	}
	
}