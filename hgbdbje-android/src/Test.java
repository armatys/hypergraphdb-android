import org.hypergraphdb.*;
import org.hypergraphdb.storage.bje.BJEStorageImplementation;

public class Test
{
	public static void main(String argv[])
	{
		HGConfiguration config = new HGConfiguration();
		config.setStoreImplementation(new BJEStorageImplementation());
		HyperGraph graph = HGEnvironment.get("/tmp/bdbjetest", config);
		graph.add("adfasdf");
		graph.close();
	}
}
