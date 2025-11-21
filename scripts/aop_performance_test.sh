#!/bin/zsh
# AOP를 활용한 인덱스 성능 측정 스크립트 (측정 전용 버전)
#
# 측정 로직 설명:
# - Response Time 측정: curl의 time_total을 사용하여 전체 HTTP 요청-응답 시간 측정
#   (DNS 조회 + TCP 연결 + 요청 전송 + 서버 처리 + 응답 수신 포함)
# - 워밍업 단계: JVM 워밍업 및 캐시 준비를 위해 기본 5회 요청 수행 (측정값 제외)
# - 실제 측정: 기본 20회 요청 수행하여 통계 계산
# - 통계 계산: 평균(avg), 최소(min), 최대(max) 시간 계산
# - AOP 연동: PerformanceLoggingAspect가 각 레이어(Controller/Service/Repository)별 실행 시간을 로그로 기록
#   (각 레이어별 상세 시간은 애플리케이션 로그에서 확인 가능)
#
# 사용법:
#   ./scripts/aop_performance_test.sh [라벨]
#   API_URL=http://localhost:8081/api/v1/feed ITERATIONS=50 ./scripts/aop_performance_test.sh "인덱스 적용 전"

API_URL="${API_URL:-http://localhost:8081/api/v1/feed}"
TEST_USER_ID="${TEST_USER_ID:-1}"
TEST_SIZE="${TEST_SIZE:-20}"
WARMUP="${WARMUP:-5}"        # 워밍업 호출 수
ITERATIONS="${ITERATIONS:-20}" # 실제 측정 호출 수
LABEL="${1:-측정}"

echo "=========================================="
echo "AOP 기반 성능 측정"
echo "=========================================="
echo "  API URL : $API_URL"
echo "  User ID : $TEST_USER_ID"
echo "  Size    : $TEST_SIZE"
echo "  워밍업   : $WARMUP 회"
echo "  측정     : $ITERATIONS 회"
echo "  라벨     : $LABEL"
echo "=========================================="
echo ""

# 측정 함수
measure_phase() {
    local label="$1"

    echo "[$label] 워밍업 ${WARMUP}회 수행중..."
    for _ in $(seq 1 "$WARMUP"); do
        curl -s -o /dev/null -w "%{time_total}\n" \
            "$API_URL?userId=$TEST_USER_ID&size=$TEST_SIZE" >/dev/null
    done

    echo "[$label] 측정 ${ITERATIONS}회 수행중..."
    local times=()
    for _ in $(seq 1 "$ITERATIONS"); do
        local t
        t=$(curl -s -o /dev/null -w "%{time_total}\n" \
            "$API_URL?userId=$TEST_USER_ID&size=$TEST_SIZE")
        echo "t=$t"
        times+=("$t")
    done

    local stats
    stats=$(
        printf "%s\n" "${times[@]}" | awk '
            NR==1 { min=max=$1 }
            { sum+=$1; if ($1 < min) min=$1; if ($1 > max) max=$1 }
            END {
                if (NR > 0) {
                    printf "%d %.6f %.6f %.6f\n", NR, sum/NR, min, max
                }
            }
        '
    )

    local count avg min max
    read -r count avg min max <<<"$stats"

    echo ""
    echo "=== 결과 ($label) ==="
    echo "  요청 수 : $count"
    echo "  평균 시간 : ${avg}s"
    echo "  최소 시간 : ${min}s"
    echo "  최대 시간 : ${max}s"
    echo "====================="
}

measure_phase "$LABEL"
