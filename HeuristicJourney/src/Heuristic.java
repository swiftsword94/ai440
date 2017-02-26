public interface Heuristic <A, B, C>
{
	public int x = 0, y = 0;
	public Double apply(A a, B b, C c);
}
