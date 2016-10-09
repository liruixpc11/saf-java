package lab.cadl.analysis.behavior.engine.utils;

/**
 * Created by lirui on 2016/5/21.
 */
public class Pair<TL, TR> {
    private TL left;
    private TR right;

    public Pair(TL left, TR right) {
        this.left = left;
        this.right = right;
    }

    public TL getLeft() {
        return left;
    }

    public TR getRight() {
        return right;
    }
}
