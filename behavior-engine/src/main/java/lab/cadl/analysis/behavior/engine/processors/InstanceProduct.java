package lab.cadl.analysis.behavior.engine.processors;

import lab.cadl.analysis.behavior.engine.instance.AnalysisInstance;
import lab.cadl.analysis.behavior.engine.instance.StateInstance;

import java.util.Arrays;
import java.util.List;

class InstanceProduct {
    private List<List<AnalysisInstance>> instancesListList;
    private int[] lengthList;
    private int[] indices;
    private AnalysisInstance[] dependeeRecord;
    private boolean finished;

    InstanceProduct(List<List<AnalysisInstance>> instancesListList) {
        this.instancesListList = instancesListList;
        if (instancesListList.stream().anyMatch(List::isEmpty)) {
            // *optimization*
            // any empty list will result in none matching
            finished = true;
            return;
        } else {
            finished = false;
        }

        // length array of instancesList
        lengthList = new int[instancesListList.size()];
        for (int i = 0; i < lengthList.length; i++) {
            lengthList[i] = instancesListList.get(i).size();
        }

        // current index of instancesList
        indices = new int[instancesListList.size()];
        Arrays.fill(indices, 0);

        // traverse of instance composite
        dependeeRecord = new StateInstance[indices.length];
    }

    AnalysisInstance[] next() {
        if (finished) {
            return null;
        }

        for (int i = 0; i < indices.length; i++) {
            dependeeRecord[i] = instancesListList.get(i).get(indices[i]);
        }

        finished = advanceIndex();
        return dependeeRecord;
    }

    private boolean advanceIndex() {
        for (int i = 0; i < indices.length; i++) {
            indices[i]++;

            if (indices[i] < lengthList[i]) {
                return false;
            } else {
                indices[i] = 0;
            }
        }

        // no more elements
        return true;
    }

}
