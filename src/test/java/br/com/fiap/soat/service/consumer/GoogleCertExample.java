package br.com.fiap.soat.service.consumer;

import java.util.HashMap;
import java.util.Map;

public class GoogleCertExample {
  
  private GoogleCertExample() {}

  public static Map<String, String> getCertificados() {
    Map<String, String> certificados = new HashMap<>();

    certificados.put("30b221ab65617bcf87ee0f8462f74e36522ca2e4", 
        """
        -----BEGIN CERTIFICATE-----
        MIIDHTCCAgWgAwIBAgIJANvroH4geY9hMA0GCSqGSIb3DQEBBQUAMDExLzAtBgNV
        BAMMJnNlY3VyZXRva2VuLnN5c3RlbS5nc2VydmljZWFjY291bnQuY29tMB4XDTI1
        MDMxNjA3MzMwMVoXDTI1MDQwMTE5NDgwMVowMTEvMC0GA1UEAwwmc2VjdXJldG9r
        ZW4uc3lzdGVtLmdzZXJ2aWNlYWNjb3VudC5jb20wggEiMA0GCSqGSIb3DQEBAQUA
        A4IBDwAwggEKAoIBAQDpmOCpEhmR8llDphAN1oeFC206CR2J3XaNz0r2XfSQeHvJ
        sUB3cnekxL49ZkMVjeQR0yHh3hkz0P3VgUPEXIIyCZQvzETNx95orGMDOZ33qDVA
        PN7g+QAeXTMHEWJkcUe6g20iPhO94W899ayQvuX5A9/kQ6vhW4Xm5v170Gfn/WZI
        k1fN6OOrk4eqaMbLr/eVFCnZcKxnJypr2F/YQszhpvjq2070gdH/1BNuk4C6+iVe
        3WHGAY1kwy5GUdmnDMg44AHCcYfztg24XHSPy6T9jaF1lPG26MZFrHSFqpro+0Q0
        AkqWmtwoQHaF8uzBjiJ5Nm96MafAseJlYAQ1USDxAgMBAAGjODA2MAwGA1UdEwEB
        /wQCMAAwDgYDVR0PAQH/BAQDAgeAMBYGA1UdJQEB/wQMMAoGCCsGAQUFBwMCMA0G
        CSqGSIb3DQEBBQUAA4IBAQBkaL0lbmHnrRDwMMTT9ZZDY2QHdFb9Gf33wy9BmFlS
        Yb27yAHaqja0iJmftqx9N5ThiijYIgPdQY6/9bpe9APEINIX/YDNOfcH4zWPi83X
        KDY1Zrz/o6mnQNCOVNXQB7KCvZ/GWfbUDPLQOM+nq24OtAN1xChinuCUMzeQJSdJ
        4H+o79MyW5IvD1x2QTzsITKpWtQOhx/CZKAUxRoIHts05oJ1eHaviyXRoT56uYC2
        N73XeQZtibUkRSJALE4A/U/wALZAkhI2Oy/6BBNX7wBpCRwVMeDtAz18qVGrcWFP
        3PTj3eEgpKVnCAm1VJWq0ad32riGMOj1M+NUe7vxFP4X
        -----END CERTIFICATE-----
        """);

    certificados.put("a9ddca76c123326b9e2e82d8ac4841e53322b76a", 
        """
        -----BEGIN CERTIFICATE-----
        MIIDHDCCAgSgAwIBAgIIbQpR1TAlrD0wDQYJKoZIhvcNAQEFBQAwMTEvMC0GA1UE
        Awwmc2VjdXJldG9rZW4uc3lzdGVtLmdzZXJ2aWNlYWNjb3VudC5jb20wHhcNMjUw
        MzI0MDczMzAyWhcNMjUwNDA5MTk0ODAyWjAxMS8wLQYDVQQDDCZzZWN1cmV0b2tl
        bi5zeXN0ZW0uZ3NlcnZpY2VhY2NvdW50LmNvbTCCASIwDQYJKoZIhvcNAQEBBQAD
        ggEPADCCAQoCggEBAKXzYaiYLfzCfeLEIHTtURWIFLsUUs+gy14hZIy5e7zT7bnj
        RVEQ9OdZvTk8H6Jws6bjD86p/+Ci7G+Aj4QuGQKv/8Act8MuxmqUH3x2ZBYZx7gD
        NRAGglw3YFIObKHP4wWxYZdsKDcN+5bO2QIHFGna4AhAjCSW4X1s9XBCSpztLSjR
        oOIaRoBMRP9PL4pIQ5B5aW4gjlMoxVi4tD2IuJmUa2obC2dVanJ5gieCzupfQEFm
        bZnmTOrYoaLvMf997SED8Mv1oUT9GKE/BBzF7vsNshGJ6RUSH6pCCTaJWh1W27o5
        OuMxXdowYCrnChcgevHV5Xate/yEx6Uo7AwmVo8CAwEAAaM4MDYwDAYDVR0TAQH/
        BAIwADAOBgNVHQ8BAf8EBAMCB4AwFgYDVR0lAQH/BAwwCgYIKwYBBQUHAwIwDQYJ
        KoZIhvcNAQEFBQADggEBADBfVmTK0LOIPtGj7mDZIWIIiAaWwZ/i9xFa/8wwBAaO
        Z5FHFASHqoDgLrWoq8wg5QamajSPkZCaTxGT4W2uDwtsrj+CV9SH2D5gMRcKgpGy
        XsQ9v32LeOx+ghftTFk40Bglzer6OvB4zitTOZj3HuGWA3xVMxNZdRIPCVb8SnAM
        kof3m5HVPICLDltK/Zbw3VqTTichCgMLNw6Jw7cb1rhQsghHoJ8GW6JTLf/qQ+Rd
        W648xd1hTDvSv2lGxkUBfPLVbkVacuKmKG6kVNPYEhfaxFl2m+GFSNFMd8zxPaco
        GsICN4oCp7Swdpnc1Gdn3ulL/jQCNen08F9KXWEwgQs=
        -----END CERTIFICATE-----
        """);

    return certificados;
  }
}
