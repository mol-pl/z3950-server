package  org.jzkit.search.provider.z3950;

/**
 *
 * ZAuthenticationMethod . ZAuthenticationMethod is an attempt to keep JZKit as flexible, yet
 * as useful as possible to the widest possible audience. When we are creating a simple test
 * client, we want to be able to create a Z3950 connection to an authenticated target using
 * some hard-coded authentication parameters. However, the real world is much more complicated,
 * and we probably need to take account new authentication systems such as Shibboleth and the 
 * UK athens system, as well as the possibility of using a local mapping of private user ids to
 * z39.50 user/group/password authentication information. Different implementations of ZAuthentication
 * method can do this for us. Ultimately, all (current) Z authentication schemes must resolve to
 * none, anon, simple, or idpass structures, which need a number of parameters. These different
 * realisations of ZAuthenticationMethod can do anything, from simply echoing static values for
 * these values, to looking up in a remote database an appropriate type/user/group/pass for the
 * userid given in the @see org.jzkit.SearchProvider.iface.ServiceUserInformation object passed to the
 * @see org.jzkit.SearchProvider.iface.SearchServiceDescription.newSearchable method.
 *
 * @version:    $Id: ZAuthenticationMethod.java,v 1.1.1.1 2004/06/18 06:38:44 ibbo Exp $
 * Copyright:   Copyright (C) 1999-2002 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 */

public abstract class ZAuthenticationMethod
{
  public abstract int getAuthType();
  public abstract String getUserId();
  public abstract String getGroupId();
  public abstract String getCredentials();
}
