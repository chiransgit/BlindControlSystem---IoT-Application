package edu.rit.csci759.fuzzylogic;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.LinguisticTerm;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.RuleBlock;
import net.sourceforge.jFuzzyLogic.rule.RuleExpression;
import net.sourceforge.jFuzzyLogic.rule.RuleTerm;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodAndMin;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodOrMax;

public class MyTipperClass {
	
	public void setRules(String ruleString, String ruleName) {
	
		String filename = "FuzzyLogic/tipper.fcl";
		FIS fis = FIS.load(filename, true);

		if(fis == null) {
			System.err.println("Cannot load fcl file");
		}

		FunctionBlock fb = fis.getFunctionBlock(null);

		String[] splitString = ruleString.split(" ");
		String temperatureValue = null;
		String ambientValue = null;
		String blindState = null;
		int condition = 0;

		for(int i=0; i<splitString.length; i++) {
						
			if(splitString[i].equals("temperature")) {
				temperatureValue = splitString[i+1];
			}
			else if(splitString[i].equals("ambient")) {
				ambientValue = splitString[i+1];
			}
			else if(splitString[i].equals("blind")) {
				blindState = splitString[i+1];
			}
			else if(splitString[i].equals("1") || splitString[i].equals("0")){
				condition = Integer.parseInt(splitString[i]);
			}
		}

		RuleBlock newBlock = fb.getFuzzyRuleBlock("No1");
		String nextName = null;

		if(ruleName.equals("0")) {
			int size = newBlock.getRules().size();
			nextName =  String.valueOf(size + 1);
		}
		else {
			nextName = ruleName;
		}
		Rule newRule = new Rule(nextName, newBlock);

		RuleTerm term1,term2;
		Variable temperature = fb.getVariable("temperature");
		Variable ambient = fb.getVariable("ambient");
		Variable blind = fb.getVariable("blind");
		

		if(condition < 2) {

			term1 = new RuleTerm(temperature, temperatureValue, false);
			term2 = new RuleTerm(ambient, ambientValue, false);
			
			if (condition == 0) {
				RuleExpression antecedentOr = new RuleExpression();
				antecedentOr.setRuleConnectionMethod(RuleConnectionMethodOrMax.get());
				antecedentOr.add(term1);
				antecedentOr.add(term2);
				newRule.setAntecedents(antecedentOr);
			}
			else {
				RuleExpression antecedentAnd = new RuleExpression();
				antecedentAnd.setRuleConnectionMethod(RuleConnectionMethodAndMin.get());
				antecedentAnd.add(term1);
				antecedentAnd.add(term2);
				newRule.setAntecedents(antecedentAnd);
			}
		}
	
		newRule.addConsequent(blind, blindState , false);
	
		newBlock.add(newRule);
		
		try {
			BufferedWriter writer = new BufferedWriter(new PrintWriter("FuzzyLogic/tipper.fcl"));
			writer.write(fis.toStringFcl());
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<String> getRules() {

		String filename = "FuzzyLogic/tipper.fcl";
                FIS fis = FIS.load(filename, true);


                if (fis == null) {
                        System.err.println("Can't load file: '" + filename + "'");
                        System.exit(1);
                }

                // Get default function block
                FunctionBlock fb = fis.getFunctionBlock(null);


                // Evaluate
                fb.evaluate();
                
     		ArrayList<String> returnList = new ArrayList<String>();

                for(Rule r: fb.getFuzzyRuleBlock("No1").getRules()) {
                        returnList.add(r.toStringFcl());
	        }

		
		return returnList;

	}

	public void updateRule(String ruleName, String newRule) {
		String filename = "FuzzyLogic/tipper.fcl";
                FIS fis = FIS.load(filename, true);


                if (fis == null) {
                        System.err.println("Can't load file: '" + filename + "'");
                        System.exit(1);
                }

                // Get default function block
                FunctionBlock fb = fis.getFunctionBlock(null);
		RuleBlock rb = fb.getFuzzyRuleBlock("No1");

		List<Rule> rules = rb.getRules();
		String updatedRuleName = null;
		Rule ruleToRemove = null;
		for (Rule rule : rules) {
			if (rule.getName().equals(ruleName)) {
				updatedRuleName = rule.getName();
				ruleToRemove = rule;	
			}
		}
		
		if(ruleToRemove != null) {
			rb.remove(ruleToRemove);
		}

		try {
                	BufferedWriter writer = new BufferedWriter(new PrintWriter("FuzzyLogic/tipper.fcl"));
                        writer.write(fis.toStringFcl());
                        writer.close();
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
		if (updatedRuleName != null) {
			setRules(newRule, updatedRuleName);
		}

	}

	public String getBlindState(float temperature, int ambient) {
	
		String filename = "FuzzyLogic/tipper.fcl";
		FIS fis = FIS.load(filename, true);

		if (fis == null) {
			System.err.println("Can't load file: '" + filename + "'");
			System.exit(1);
		}

		// Get default function block
		FunctionBlock fb = fis.getFunctionBlock(null);

		// Set inputs
		fb.setVariable("temperature", temperature);
		fb.setVariable("ambient", ambient);

		// Evaluate
		fb.evaluate();

		// Show output variable's chart
		fb.getVariable("blind").defuzzify();

		
		HashMap<String, LinguisticTerm> terms = fb.getVariable("blind").getLinguisticTerms();
		String highestTerm = null;
		double highestVal = 0;
		for (String term : terms.keySet()) {
			double curVal = fb.getVariable("blind").getMembership(term);
			
			if (curVal > highestVal) {
				highestVal = curVal;
				highestTerm = term;
			}
		}

		System.out.println("Highest term val for: " + highestTerm);

		for(Rule r: fb.getFuzzyRuleBlock("No1").getRules()) {
			System.out.println(r.toStringFcl());
		}

		return highestTerm;

	}

}
