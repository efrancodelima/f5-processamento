#!/bin/bash

COMPONENT_KEY='efrancodelima_producao'

get_first_sonar_metric() {
  local metric_key="$1"
  local response=$(curl --location "https://sonarcloud.io/api/measures/component?metricKeys=${metric_key}&component=${COMPONENT_KEY}" \
  --header "Authorization: Bearer ${SONAR_TOKEN}" 2>/dev/null)

  echo "${response}" | jq .component.measures[0].periods[0].value
}

get_sonar_metric() {
  local metric_key="$1"
  local response=$(curl --location "https://sonarcloud.io/api/measures/component?metricKeys=${metric_key}&component=${COMPONENT_KEY}" \
  --header "Authorization: Bearer ${SONAR_TOKEN}" 2>/dev/null)

  echo "${response}" | jq .component.measures[0].value
}

remove_double_quotes() {
  local input_string="$1"
  local output_string="${input_string//\"/}"
  echo "$output_string"
}

# # # # # # # # # #  Coverage  # # # # # # # # # #
echo ""
echo "Coverage analysis"

# Test errors
test_errors=$(get_first_sonar_metric "test_errors")
test_errors=$(remove_double_quotes $test_errors)

if [ "$(echo "$test_errors == 0" | bc -l)" -eq 1 ]; then
  echo "- Test errors: 0 (ok)"
else
  echo "- Test errors: ${test_errors} (fail)"
  exit 1
fi

# Test failures
test_failures=$(get_first_sonar_metric "test_failures")
test_failures=$(remove_double_quotes $test_failures)

if [ "$(echo "$test_failures == 0" | bc -l)" -eq 1 ]; then
  echo "- Test failures: 0 (ok)"
else
  echo "- Test failures: ${test_failures} (fail)"
  exit 1
fi

# Coverage
coverage=$(get_sonar_metric "coverage")
coverage=$(remove_double_quotes $coverage)

if [ "$(echo "$coverage > 80" | bc -l)" -eq 1 ]; then
  echo "- Coverage: ${coverage} (ok)"
else
  echo "- Coverage: ${coverage} (fail)"
  exit 1
fi

# Line coverage
line_coverage=$(get_sonar_metric "line_coverage")
line_coverage=$(remove_double_quotes $line_coverage)

if [ "$(echo "$line_coverage > 80" | bc -l)" -eq 1 ]; then
  echo "- Line coverage: ${line_coverage} (ok)"
else
  echo "- Line coverage: ${line_coverage} (fail)"
  exit 1
fi

# # # # # # # # # #  Security  # # # # # # # # # #

echo ""
echo "Security analysis"

# New vulnerabilities
vulnerabilities=$(get_sonar_metric "vulnerabilities")
vulnerabilities=$(remove_double_quotes $vulnerabilities)
echo "- Vulnerabilities: ${vulnerabilities}"

new_vulnerabilities=$(get_first_sonar_metric "new_vulnerabilities")
new_vulnerabilities=$(remove_double_quotes $new_vulnerabilities)

if [ "$(echo "$new_vulnerabilities == 0" | bc -l)" -eq 1 ]; then
  echo "- New vulnerabilities: ${new_vulnerabilities} (ok)"
else
  echo "- New vulnerabilities: ${new_vulnerabilities} (fail)"
  exit 1
fi

# New security hotspots
sec_hotspots=$(get_sonar_metric "security_hotspots")
sec_hotspots=$(remove_double_quotes $sec_hotspots)
echo "- Security hotspots: ${sec_hotspots}"

new_sec_hotspots=$(get_first_sonar_metric "new_security_hotspots")
new_sec_hotspots=$(remove_double_quotes $new_sec_hotspots)

if [ "$(echo "$new_sec_hotspots == 0" | bc -l)" -eq 1 ]; then
  echo "- New security hotspots: ${new_sec_hotspots} (ok)"
else
  echo "- New security hotspots: ${new_sec_hotspots} (fail)"
  exit 1
fi

# Security rating
security_rating=$(get_sonar_metric "security_rating")
security_rating=$(remove_double_quotes $security_rating)

if [ "$(echo "$security_rating == 1" | bc -l)" -eq 1 ]; then
  echo "- Security rating: A (ok)"
else
  echo "- Security rating: ${security_rating} (fail)"
  exit 1
fi

# # # # # # # # # #  Reliability  # # # # # # # # # #

echo ""
echo "Reliability analysis"

# New bugs
bugs=$(get_sonar_metric "bugs")
bugs=$(remove_double_quotes $bugs)
echo "- Bugs: ${bugs}"

new_bugs=$(get_first_sonar_metric "new_bugs")
new_bugs=$(remove_double_quotes $new_bugs)

if [ "$(echo "$new_bugs == 0" | bc -l)" -eq 1 ]; then
  echo "- New bugs: ${new_bugs} (ok)"
else
  echo "- New bugs: ${new_bugs} (fail)"
  exit 1
fi

# Reliability rating
reliability_rating=$(get_sonar_metric "reliability_rating")
reliability_rating=$(remove_double_quotes $reliability_rating)

if [ "$(echo "$reliability_rating == 1" | bc -l)" -eq 1 ]; then
  echo "- Reliability rating: A (ok)"
else
  echo "- Reliability rating: ${reliability_rating} (fail)"
  exit 1
fi

# # # # # # # # # #  Maintainability  # # # # # # # # # #

echo ""
echo "Maintainability analysis"

# New code smells
code_smells=$(get_sonar_metric "code_smells")
code_smells=$(remove_double_quotes $code_smells)
echo "- Code smells: ${code_smells}"

new_code_smells=$(get_first_sonar_metric "new_code_smells")
new_code_smells=$(remove_double_quotes $new_code_smells)

if [ "$(echo "$new_code_smells == 0" | bc -l)" -eq 1 ]; then
  echo "- New code smells: ${new_code_smells} (ok)"
else
  echo "- New code smells: ${new_code_smells} (fail)"
  exit 1
fi

# Maintainability rating
sqale_rating=$(get_sonar_metric "sqale_rating")
sqale_rating=$(remove_double_quotes $sqale_rating)

if [ "$(echo "$sqale_rating == 1" | bc -l)" -eq 1 ]; then
  echo "- Maintainability rating: A (ok)"
else
  echo "- Maintainability rating: ${sqale_rating} (fail)"
  exit 1
fi

# # # # # # # # # #  Quality gate  # # # # # # # # # #

echo ""
echo "Quality gate"

# New code smells
alert_status=$(get_sonar_metric "alert_status")
alert_status=$(remove_double_quotes $alert_status)

if [ "$alert_status" = "OK" ]; then
  echo "- Status: ${alert_status}"
else
  echo "- Status: ${alert_status}"
  exit 1
fi
