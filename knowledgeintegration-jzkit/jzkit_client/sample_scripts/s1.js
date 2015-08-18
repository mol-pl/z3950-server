jzList("sessions");
s1 = jzNewSession();
jzList("sessions");
rs = s1.search(new org.jzkit.search.landscape.SimpleLandscapeSpecification("c1,c2"),new org.jzkit.search.util.QueryModel.PrefixString.PrefixString("@attr 1=4 science"),null,null,null);
s1.close();
unset("s1");
jzTidy();
jzList("sessions");
