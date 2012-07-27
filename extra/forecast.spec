Name:		forecast
Version:	0.5
Release:	1%{?dist}
Summary:	Forecast is a Red Hat Enterprise Linux metering tool for various virtualisation platforms
Packager:	Rhys Oxenham <roxenham@redhat.com>

Group:		Applications/System
License:	GPLv2+
URL:		http://www.github.com/rdoxenham/forecast

Requires:	PyGreSQL
Requires:	postgresql
Requires:	postgresql-jdbc
Requires:	postgresql-server
Requires:	jakarta-commons-daemon-jsvc
Requires:	python-lxml

%description

Forecast is a virtualisation or cloud based monitoring solution for Red Hat Enterprise Linux guests. Enabling charge-back and reporting of utilisation across multiple different platforms simultaneously.

%prep

%build

%install

%clean

%files
%defattr(-,root,root,-)
/usr/share/forecast/forecast.conf.sample
/usr/share/forecast/forecast.sql
/opt/forecast/
%attr(4755, root, root) /etc/init.d/forecast-service
%attr(4755, root, root) /usr/bin/forecast


%changelog
* Fri Apr 13 2012 Rhys Oxenham <roxenham@redhat.com> 0.5-1
- Extended functionality to do memory and/or cpu based pricing
- Finished --export option which exports statistics to XML
- Modified all Java and database scripts to accomodate the changes

* Sun Apr 01 2012 Rhys Oxenham <roxenham@redhat.com> 0.3-1
- Implemented daemon to control forecast, introduced basic export features

* Tue Mar 27 2012 Rhys Oxenham <roxenham@redhat.com> 0.2-1
- Finished Python CLI and created basic service control script

* Tue Mar 27 2012 Rhys Oxenham <roxenham@redhat.com> 0.1-2
- Fixed a lot of bugs from early testing

* Mon Mar 26 2012 Rhys Oxenham <roxenham@redhat.com> 0.1-1
- Initial release for alpha testing
