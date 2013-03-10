package opt.test;

/**
 * Created with IntelliJ IDEA.
 * User: ben
 * Date: 3/9/13
 * Time: 7:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class Run {
    private double _value;
    private double _time;
    public Run(double value, double time){
        _value=value;
        _time=time;
    }
    public double getTime() {
        return _time;
    }

    public void setTime(double _time) {
        this._time = _time;
    }

    public double getValue() {
        return _value;
    }

    public void setValue(double _value) {
        this._value = _value;
    }
}
