# smart-tv-spoof

Some smart TVs check vendor-specific HTTP endpoints to confirm internet connectivity before launching apps. When
DNS-level ad blockers (NextDNS, AdGuard, Pi-hole) block these domains, the TV thinks it's offline and refuses to open
some apps — even though those servers are reachable.

**smart-tv-spoof** serves fake responses to these connectivity checks so the TV thinks it's online while your DNS
blocking stays active.

## DNS Configuration

Redirect the connectivity-check domains to the IP address running smart-tv-spoof:

- `cdn.samsungcloudsolution.com`
- `time.samsungcloudsolution.com`
