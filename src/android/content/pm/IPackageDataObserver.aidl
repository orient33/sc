package android.content.pm;
/**
 {@hide}
*/
oneway interface IPackageDataObserver{
    void onRemoveCompleted(in String pkgName, boolean successed);
}