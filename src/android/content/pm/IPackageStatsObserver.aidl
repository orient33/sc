package android.content.pm;
import android.content.pm.PackageStats;
/** 
 {@hide}
 */
oneway interface IPackageStatsObserver{
  void onGetStatsCompleted(in PackageStats ps, boolean s);
}