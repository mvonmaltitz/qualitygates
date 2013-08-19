package de.binarytree.plugins.qualitygates.steps.manualcheck;

import hudson.model.Result;
import de.binarytree.plugins.qualitygates.GateStep;
import de.binarytree.plugins.qualitygates.result.GateReport;
import de.binarytree.plugins.qualitygates.result.GateStepReport;
import de.binarytree.plugins.qualitygates.result.QualityLineReport;

/**
 * This class holds the algorithm to find the next unapproved manual check which
 * can then be approved or disapproved.
 * 
 * @author Marcel von Maltitz
 * 
 */
public class ManualCheckFinder {
    /**
     * This class holds the found manual check. This check can then be approved
     * or disapproved with the help of this class.
     * 
     * @author Marcel von Maltitz
     * 
     */
    public static class ManualCheckManipulator{
        private ManualCheck check;
        ManualCheckManipulator(ManualCheck check){
            this.check = check; 
        }
        ManualCheckManipulator(){}
        
        public void approve(){
            this.check.approve(); 
        }
       public void disapprove(){
           this.check.disapprove(); 
       } 
        public boolean hasItem(){
            return this.check != null; 
        }
    }
    
    private QualityLineReport qualityLineReport;

    public ManualCheckFinder(QualityLineReport qualityLineReport) {
        this.qualityLineReport = qualityLineReport;
    }

    public ManualCheckManipulator findCheckForGivenHash(String hashIdOfCheck) {
        GateReport unbuiltGate = this.getNextUnbuiltGate(qualityLineReport);
        if (unbuiltGate != null) {
            return findNextManualUnbuiltCheckIfExists(hashIdOfCheck, unbuiltGate);
        }
        return this.createNullManipulator(); 
    }


    private ManualCheckManipulator findNextManualUnbuiltCheckIfExists(
            String hashIdOfCheck, GateReport unbuiltGate) {
        GateStepReport reportOfNextUnbuiltStep = this
                .getNextUnbuiltStep(unbuiltGate);
        GateStep step = reportOfNextUnbuiltStep.getStep();
        if (step instanceof ManualCheck) {
            return getCheckIfHashMatches(hashIdOfCheck,
                    (ManualCheck) step);
        } else {
        return this.createNullManipulator(); 
        }
    }

    private ManualCheckManipulator getCheckIfHashMatches(
            String hashIdOfCheck, ManualCheck manualCheck) {
        if (manualCheck.hasHash(hashIdOfCheck)) {
            return new ManualCheckManipulator(manualCheck);
        }
        return this.createNullManipulator(); 
    }

    private ManualCheckManipulator createNullManipulator(){
        return new ManualCheckManipulator();
    }
    public GateReport getNextUnbuiltGate(QualityLineReport qualityLineReport) {
        for (GateReport gateReport : qualityLineReport.getGateReports()) {
            if (isPassedGate(gateReport)) {
                continue;
            } else if (isNotBuilt(gateReport)) {
                return gateReport;
            } else {
                return null;
            }
        }
        return null;
    }

    private boolean isNotBuilt(GateReport gateReport) {
        return gateReport.getResult().equals(Result.NOT_BUILT);
    }

    private boolean isPassedGate(GateReport gateReport) {
        return gateReport.getResult().isBetterOrEqualTo(Result.UNSTABLE);
    }

    public GateStepReport getNextUnbuiltStep(GateReport gateReport) {
        for (GateStepReport stepReport : gateReport.getStepReports()) {
            if (isNotBuilt(stepReport)) {
                return stepReport;
            }
        }
        return null;
    }

    private boolean isNotBuilt(GateStepReport stepReport) {
        return stepReport.getResult().equals(Result.NOT_BUILT);
    }
}
