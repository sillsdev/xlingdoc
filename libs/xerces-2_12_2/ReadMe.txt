Note:

We had to remove org/w3c/dom/html/HTMLDOMImplementation.class from xercesImpl in order to avoid a runtime error.  The org.w3c.dom.html package inside xercesImpl.jar is directly colliding with the same package in the JDK's jdk.xml.dom module. This is a known, unresolved issue in xercesImpl versions prior to 2.12.3 (and often persists even in 2.12.x depending on the build).
