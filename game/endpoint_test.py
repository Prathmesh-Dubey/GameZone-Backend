import requests

base = 'https://gamezone-cf3p2jpe5a-uc.a.run.app'

cases = [
    ('GET', '/'),
    ('GET', '/auth/user'),
    ('POST', '/auth/login'),
    ('POST', '/auth/register'),
    ('GET', '/api/users'),
    ('GET', '/api/users/user-123'),
    ('PUT', '/api/users/user-123'),
    ('DELETE', '/api/users/user-123'),
    ('GET', '/api/profiles/user-123'),
    ('POST', '/api/profiles'),
    ('PUT', '/api/profiles/user-123'),
    ('DELETE', '/api/profiles/user-123'),
    ('GET', '/api/games'),
    ('GET', '/api/games/simulators'),
    ('GET', '/api/games/active'),
    ('GET', '/api/games/game-123'),
    ('GET', '/api/games/game-123/code'),
    ('POST', '/api/games'),
    ('PUT', '/api/games/game-123'),
    ('DELETE', '/api/games/game-123'),
    ('POST', '/api/sessions/start'),
    ('PUT', '/api/sessions/end/session-123'),
    ('GET', '/api/sessions/user/user-123'),
    ('GET', '/api/sessions/game/game-123'),
    ('GET', '/api/sessions/active/user/user-123'),
    ('GET', '/api/sessions/total-time/user/user-123'),
    ('GET', '/api/sessions/dau'),
    ('GET', '/api/sessions/count/user/user-123'),
    ('POST', '/api/scores'),
    ('GET', '/api/scores/game/game-123'),
    ('GET', '/api/scores/user/user-123'),
    ('GET', '/api/scores/leaderboard/game-123'),
    ('GET', '/api/scores/global-leaderboard'),
    ('GET', '/api/scores/personal-best'),
    ('GET', '/api/achievements'),
    ('GET', '/api/achievements/achievement-123'),
    ('POST', '/api/achievements'),
    ('PUT', '/api/achievements/achievement-123'),
    ('DELETE', '/api/achievements/achievement-123'),
    ('POST', '/api/user-achievements/unlock'),
    ('GET', '/api/user-achievements/user/user-123'),
    ('GET', '/api/user-achievements/achievement/achievement-123'),
    ('GET', '/api/user-achievements/count/user/user-123'),
    ('POST', '/api/user-achievements/check'),
    ('GET', '/api/notifications/active'),
    ('GET', '/api/notifications'),
    ('POST', '/api/notifications'),
    ('PUT', '/api/notifications/notification-123'),
    ('DELETE', '/api/notifications/notification-123'),
    ('PATCH', '/api/notifications/notification-123/toggle'),
    ('GET', '/api/simulators'),
    ('GET', '/api/simulators/active'),
    ('GET', '/api/simulators/simulator-123'),
    ('GET', '/api/simulators/simulator-123/code'),
    ('POST', '/api/simulators'),
    ('PUT', '/api/simulators/simulator-123'),
    ('DELETE', '/api/simulators/simulator-123'),
    ('GET', '/does-not-exist'),
]

for method, path in cases:
    try:
        if method == 'GET':
            r = requests.get(base + path, timeout=20)
        elif method == 'POST':
            r = requests.post(base + path, json={}, timeout=20)
        elif method == 'PUT':
            r = requests.put(base + path, json={}, timeout=20)
        elif method == 'PATCH':
            r = requests.patch(base + path, json={}, timeout=20)
        elif method == 'DELETE':
            r = requests.delete(base + path, timeout=20)
        print(f'{method} {path} -> {r.status_code} {r.text[:220].replace(chr(10), " ")}')
    except Exception as e:
        print(f'{method} {path} -> ERROR {e}')
