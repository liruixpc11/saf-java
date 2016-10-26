[header]
NAMESPACE = NET.APP_PROTO
NAME = HTTPDEMO
QUALIFIER = {eventtype='PACKET_TCP'}

[states]
tcp_req = {protocol=6}
http_req = {tcp_req(dport=80)}
http_res = {sport=80, dport=$http_req.sport}
# dns_req = {UDPPKTPAIR.udp_pkt_sd(dnsid=$1, dnsqrflag=0, dnsquesname=$2, dport=53)}
# dns_res = {UDPPKTPAIR.udp_pkt_ds($dns_req, dnsid=$dns_req.dnsid, dnsquesname=$dns_req.dnsquesname, dnsqrflag=1, sport=53)}

[behavior]
# b = dns_req ~> dns_res
b = http_req ~> http_res

[model]
# DNS_REQ_RES(eventno, timestamp, timestampusec, sipaddr,dipaddr,sport,dport,dnsquesname,dnsid,dnsqrflag) =  b
HTTP_REQ_RES(eventno, timestamp, timestampusec, sipaddr,dipaddr,sport,dport) =  b
