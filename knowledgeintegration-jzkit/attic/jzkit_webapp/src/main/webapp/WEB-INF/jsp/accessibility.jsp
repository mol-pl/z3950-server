<%
  String base_dir = request.getContextPath();
%>

<h2>Accessibility features</h2>
<ul>
  <li><a href="#general">General Statement</a></li>
  <li><a href="#external">External Website Links</a></li>
  <li><a href="#alttext">Alternative Text On Images</a></li>
  <li><a href="#browser">Advice On Browser Accessibility Options</a></li>
</ul>

<a name="general">&nbsp;</a>
<h3>General Statement</h3>
<p>This site has been tested to a level of <q>AAA</q> using the accessibility standard testing applications at <a href="http://www.tawdis.net">TAW</a> and <a href="http://webxact.watchfire.com/">Webxact</a>.
<br/><br/>
Care has also been taken to comply with the <a href="http://www.w3.org/">World Wide Web Consortium's</a> standards of <a href="http://www.w3.org/MarkUp/"><abbr title="eXtensible HyperText Markup Language">XHTML 1.0 Transitional</abbr></a> and <a href="http://www.w3.org/Style/CSS/"><abbr title="Cascading Style Sheets">CSS</abbr> 2.0</a>. Click either of the buttons at the <a href="#bottom">bottom of the page</a> to view the respective validator.</p>

<p>The entire site has been tested for functionality and layout on the following operating systems and browsers:</p>
<ul>
<li>GNU/Linux - Firefox 1.5.06</li>
<li>GNU/Linux - Mozilla 1.7.13</li>
<li>Mac OSX - Safai 1.3.1</li>
<li>Mac OSX - Opera 9.01</li>
<li>Mac OSX - Mozilla 1.7</li>
<li>Microsoft Windows - Firefox 1.5.06</li>
<li>Microsoft Windows - Internet Explorer 6</li>
</ul>

<p>Please <a href="<%=base_dir%>/contact">contact us</a> if any descrepancies to the standards mentioned above are found.
<br/><br/>
The following features are designed to make this site more accessible. 
</p>

<a name="external">&nbsp;</a>
<h3>External Website Links</h3>
<p>By default all links to external websites open in the same window. Users can override this by right clicking on the link with the mouse, then click Open link in a new window.
<br/><br/>
If you see this image <img alt="Open link in new window" title="Open link in new window" src="<%=base_dir%>/images/new.gif" height="10" width="10"/>&nbsp; next to a link you can click it to load the link in a new browser window rather than the current one.
</p>

<a name="alttext">&nbsp;</a>
<h3>Alternative Text On Images</h3>
<p>The images on this site have been assigned 'alt' text to provide information for those who are unable to view website images.</p>

<a name="browser">&nbsp;</a>
<h3>Advice On Browser Accessibility Options</h3>
<p>Settings within your browser can be changed so that the appearance of a web page suits your personal viewing needs. The process for changing your settings varies according to your browser type and version. The following provides a summary of how to change your settings in Mozilla Firefox, Microsoft Internet Explorer and Netscape Communicator.</p>

<a name="firefox">&nbsp;</a>
<h4>Mozilla Firefox</h4>
<ul>
<li>You can change size of fonts displayed on the page by opening the View menu in your browser; select Text Size; then choose your required change. This can also be achieved by holding the control [Ctrl] buttom and pressing the minus [-] or plus [+] keys. If your mouse has a scroll button, you can also hold change the text size by holding control [Ctrl] and using the scroll button.</li>
<li>You can choose to display the website without the chosen styles and layout by opening the View menu of your browser and entering Page Style; choosing No Style will remove styles and Basic Page Style will return them. Care has been taken to ensure all pages can be viewed with or without styling for use in screen readers and text based browsers.</li>
<li>You can choose to not to display images shown on websites  by opening the edit menu and choosing preferences. Selecting the Content tab allows you to choose to block images and popup windows, deny website's security privileges, and enable or disable Java or Javascript.</li>
</ul>

<a name="ie">&nbsp;</a>
<h4>Microsoft Internet Explorer</h4>
<ul>
  <li>You can make changes to colours, fonts and text size if you go to the Tools menu in your browser; select Internet Options; then the General tab to make your selections.</li>
  <li>You can prevent graphics from appearing on your web pages by going to Tools; Internet Options; Advanced tab; Multimedia heading; then remove chosen graphics options; click Apply and then OK.</li>
   <li>You can change the size of your browser window by going to View; Full Screen, or simply press F11 on your keyboard which does the same thing.</li>
</ul>

<a name="netscape">&nbsp;</a>
<h4>Netscape Communicator</h4>
<ul>
  <li>To enlarge text, go to the View menu; select Text Zoom; then choose font size.</li>
  <li>To make any other changes (such as text font, colours and background colours, preventing images from loading) to your browser settings, go to Edit; Preferences.</li>
</ul>
<a name="bottom">&nbsp;</a>
<div id="validators">
  <a href="http://validator.w3.org/check?uri=referer">
    <img src="http://www.w3.org/Icons/valid-xhtml10" title="Valid XHTML 1.0 Transitional" alt="Valid XHTML 1.0 Transitional"/>
  </a>
  &nbsp;
  <a href="http://jigsaw.w3.org/css-validator/">
    <img src="http://jigsaw.w3.org/css-validator/images/vcss" title="Valid CSS" alt="Valid CSS"/>
  </a>
</div>
