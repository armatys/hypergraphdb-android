# HypergraphDB for Android&trade;

## Installation

Download the `.jar` file, put it in your libs directory, and reference it during the build process.

## Usage

There are (almost) no differences compared to the original HyperGraphDB. Please refer to documentation at [official HypergraphDB page](http://hypergraphdb.org).

The only thing you should do is to make a special call, when your Android app is low on memory. See the example below (and the overriden `onLowMemory` method):

```
import android.app.Application;
import org.hypergraphdb.HGEnvironment;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.util.MemoryWarningSystem;

public class ClassDojoApplication extends Application {
  private HyperGraph mHyperGraphDB;

  @Override
  public void onCreate() {
    File dbPath = getDatabasePath("my_database");
    mHyperGraphDB = HGEnvironment.get(mPath.getPath());
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    MemoryWarningSystem.notifyLowMemory();
  }
}
```

## License

The library is distributed under LGPL (see accompanying `LICENSE.md` file at the root level of the distribution).

The library consists of a several components, each with a given license:

- `hgdbdje-android` LGPL
- `hgdb-android` LGPL
- `openbeans` Apache License Version 2.0
- `libs/je-*.jar` (Berkeley DB) see `libs/je-LICENSE` file

##### _Android is a trademark of Google Inc._
