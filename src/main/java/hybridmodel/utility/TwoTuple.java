package hybridmodel.utility;

public class TwoTuple<One, Two> {

	private final One one;
	private final Two two;

	public TwoTuple(One one, Two two) {
		this.one = one;
		this.two = two;
	}

	public One getOne() {
		return one;
	}

	public Two getTwo() {
		return two;
	}
}
