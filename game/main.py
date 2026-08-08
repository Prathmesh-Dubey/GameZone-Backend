"""
GameZone – Firebase Functions v2 (Python 2nd Gen) backend.
Single-file Flask + Supabase replacement for the Spring Boot project.

Endpoints preserved (all original paths):
  Auth:             GET /auth/user, POST /auth/login, POST /auth/register
  Users:            GET /api/users, GET /api/users/<id>, PUT /api/users/<id>, DELETE /api/users/<id>
  Profiles:         GET /api/profiles/<userId>, POST /api/profiles, PUT /api/profiles/<userId>, DELETE /api/profiles/<userId>
  Games:            GET /api/games, GET /api/games/simulators, GET /api/games/active,
                    GET /api/games/<id>, GET /api/games/<id>/code,
                    POST /api/games, PUT /api/games/<id>, DELETE /api/games/<id>
  Sessions:         POST /api/sessions/start, PUT /api/sessions/end/<id>,
                    GET /api/sessions/user/<id>, GET /api/sessions/game/<id>,
                    GET /api/sessions/active/user/<id>, GET /api/sessions/total-time/user/<id>,
                    GET /api/sessions/dau, GET /api/sessions/count/user/<id>
  Scores:           POST /api/scores, GET /api/scores/game/<id>, GET /api/scores/user/<id>,
                    GET /api/scores/leaderboard/<id>, GET /api/scores/global-leaderboard,
                    GET /api/scores/personal-best
  Achievements:     GET /api/achievements, GET /api/achievements/<id>,
                    POST /api/achievements, PUT /api/achievements/<id>, DELETE /api/achievements/<id>
  UserAchievements: POST /api/user-achievements/unlock, GET /api/user-achievements/user/<id>,
                    GET /api/user-achievements/achievement/<id>,
                    GET /api/user-achievements/count/user/<id>,
                    POST /api/user-achievements/check
  Notifications:    GET /api/notifications/active, GET /api/notifications,
                    POST /api/notifications, PUT /api/notifications/<id>,
                    DELETE /api/notifications/<id>, PATCH /api/notifications/<id>/toggle
  Simulators:       GET /api/simulators, GET /api/simulators/active, GET /api/simulators/<id>,
                    GET /api/simulators/<id>/code,
                    POST /api/simulators, PUT /api/simulators/<id>, DELETE /api/simulators/<id>
"""

import os
import uuid
from datetime import datetime, timezone, date
from functools import wraps

from dotenv import load_dotenv
from flask import Flask, jsonify, request
from firebase_functions import https_fn
from firebase_functions.options import CorsOptions
from supabase import create_client, Client

load_dotenv(dotenv_path=".env.local", override=False)

# ---------------------------------------------------------------------------
# Supabase client
# ---------------------------------------------------------------------------
SUPABASE_URL: str | None = os.getenv("SUPABASE_URL") or os.getenv("SUPABASE_URL_LOCAL")
SUPABASE_KEY: str | None = os.getenv("SUPABASE_SERVICE_ROLE_KEY") or os.getenv("SUPABASE_KEY") or os.getenv("SUPABASE_SERVICE_ROLE_KEY_LOCAL")
ADMIN_REGISTRATION_KEY: str = os.getenv("ADMIN_REGISTRATION_KEY", "prathm123")


class SupabaseProxy:
    """Lazily initialize the Supabase client and raise a clear error if env vars are missing."""

    def __init__(self):
        self._client: Client | None = None

    def _ensure_client(self) -> Client:
        if self._client is None:
            if not SUPABASE_URL or not SUPABASE_KEY:
                raise RuntimeError("Supabase environment variables are not configured.")
            self._client = create_client(SUPABASE_URL, SUPABASE_KEY)
        return self._client

    def __getattr__(self, item):
        return getattr(self._ensure_client(), item)


supabase = SupabaseProxy()

# ---------------------------------------------------------------------------
# Flask app
# ---------------------------------------------------------------------------
app = Flask(__name__)


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def _now_iso() -> str:
    return datetime.now(timezone.utc).isoformat()


def _err(message: str, status: int = 400):
    return jsonify({
        "timestamp": _now_iso(),
        "status": status,
        "error": "Bad Request" if status == 400 else "Not Found" if status == 404 else "Error",
        "message": message,
    }), status


def _one_or_404(table: str, id_val: str, id_col: str = "id"):
    """Fetch a single row by id, returning (row, None) or (None, 404 response)."""
    res = supabase.table(table).select("*").eq(id_col, id_val).maybe_single().execute()
    if res.data is None:
        return None, _err(f"{table.rstrip('s').capitalize()} not found", 404)
    return res.data, None


# ---------------------------------------------------------------------------
# Mapper helpers
# ---------------------------------------------------------------------------

def _user_with_profile(user: dict, profile: dict | None) -> dict:
    """Merge user + profile into UserDTO shape. Password is never returned."""
    return {
        "id": user["id"],
        "username": user["username"],
        "email": user["email"],
        "role": user["role"],
        "createdAt": user.get("created_at"),
        "password": None,
        "profileId": profile["id"] if profile else None,
        "bio": profile["bio"] if profile else None,
        "avatarUrl": profile["avatar_url"] if profile else None,
        "location": profile["location"] if profile else None,
        "dateOfBirth": profile["date_of_birth"] if profile else None,
        "website": profile["website"] if profile else None,
        "accentColor": profile["accent_color"] if profile else None,
        "avatarSeed": profile["avatar_seed"] if profile else None,
    }


def _profile_response(profile: dict) -> dict:
    return {
        "id": profile["id"],
        "userId": profile["user_id"],
        "bio": profile.get("bio"),
        "avatarUrl": profile.get("avatar_url"),
        "location": profile.get("location"),
        "dateOfBirth": profile.get("date_of_birth"),
        "website": profile.get("website"),
        "accentColor": profile.get("accent_color"),
        "avatarSeed": profile.get("avatar_seed"),
        "createdAt": profile.get("created_at"),
        "updatedAt": profile.get("updated_at"),
    }


def _game_response(g: dict) -> dict:
    return {
        "id": g["id"],
        "title": g.get("title"),
        "description": g.get("description"),
        "category": g.get("category"),
        "thumbnail": g.get("thumbnail"),
        "active": g.get("active"),
        "gameCode": g.get("game_code"),
        "isDynamic": g.get("is_dynamic"),
        "type": g.get("type"),
        "createdAt": g.get("created_at"),
        "updatedAt": g.get("updated_at"),
    }


def _session_response(s: dict) -> dict:
    return {
        "id": s["id"],
        "startTime": s.get("start_time"),
        "endTime": s.get("end_time"),
        "duration": s.get("duration"),
        "userId": s.get("user_id"),
        "gameId": s.get("game_id"),
    }


def _score_response(s: dict) -> dict:
    return {
        "id": s["id"],
        "scoreValue": s.get("score_value"),
        "playedAt": s.get("played_at"),
        "userId": s.get("user_id"),
        "gameId": s.get("game_id"),
    }


def _achievement_response(a: dict) -> dict:
    return {
        "id": a["id"],
        "title": a.get("title"),
        "description": a.get("description"),
        "requiredScore": a.get("required_score"),
        "createdAt": a.get("created_at"),
        "updatedAt": a.get("updated_at"),
    }


def _user_achievement_response(ua: dict) -> dict:
    return {
        "id": ua["id"],
        "userId": ua.get("user_id"),
        "achievementId": ua.get("achievement_id"),
        "achievementTitle": ua.get("achievement_title"),
        "achievementDescription": ua.get("achievement_description"),
        "unlockedAt": ua.get("unlocked_at"),
    }


def _notification_response(n: dict) -> dict:
    return {
        "id": n["id"],
        "title": n.get("title"),
        "message": n.get("message"),
        "type": n.get("type"),
        "createdByUsername": n.get("created_by_username"),
        "createdAt": n.get("created_at"),
        "active": n.get("active"),
        "expiresAt": n.get("expires_at"),
    }


def _simulator_response(s: dict) -> dict:
    return {
        "id": s["id"],
        "title": s.get("title"),
        "description": s.get("description"),
        "category": s.get("category"),
        "thumbnail": s.get("thumbnail"),
        "active": s.get("active"),
        "simulatorCode": s.get("simulator_code"),
        "isDynamic": s.get("is_dynamic"),
        "type": s.get("type"),
        "createdAt": s.get("created_at"),
        "updatedAt": s.get("updated_at"),
    }


def _get_profile_for_user(user_id: str) -> dict | None:
    res = supabase.table("profiles").select("*").eq("user_id", user_id).maybe_single().execute()
    return res.data


# ---------------------------------------------------------------------------
# ── AUTH ──────────────────────────────────────────────────────────────────
# ---------------------------------------------------------------------------

@app.route("/auth/user", methods=["GET"])
def auth_get_user():
    identifier = request.args.get("identifier", "").strip()
    if not identifier:
        users = supabase.table("users").select("*").execute().data or []
        result = []
        for u in users:
            profile = _get_profile_for_user(u["id"])
            result.append(_user_with_profile(u, profile))
        return jsonify(result), 200

    if "@" in identifier:
        res = supabase.table("users").select("*").eq("email", identifier).maybe_single().execute()
    else:
        res = supabase.table("users").select("*").eq("username", identifier).maybe_single().execute()

    if not res.data:
        return _err("User not found", 404)
    user = res.data
    profile = _get_profile_for_user(user["id"])
    return jsonify(_user_with_profile(user, profile)), 200


@app.route("/auth/login", methods=["POST"])
def auth_login():
    body = request.get_json(force=True) or {}
    identifier = body.get("identifier", "").strip()
    password = body.get("password", "")

    if not identifier or not password:
        return _err("identifier and password are required")

    if "@" in identifier:
        res = supabase.table("users").select("*").eq("email", identifier).maybe_single().execute()
        if not res.data:
            return _err("Invalid email or password")
    else:
        res = supabase.table("users").select("*").eq("username", identifier).maybe_single().execute()
        if not res.data:
            return _err("Invalid username or password")

    user = res.data
    if user["password"] != password:
        return _err("Invalid password")

    profile = _get_profile_for_user(user["id"])
    return jsonify(_user_with_profile(user, profile)), 200


@app.route("/auth/register", methods=["POST"])
def auth_register():
    body = request.get_json(force=True) or {}
    username = body.get("username", "").strip()
    email = body.get("email", "").strip()
    password = body.get("password", "")
    role = (body.get("role") or "USER").upper()
    admin_key = body.get("adminKey", "")

    if not username or not email or not password:
        return _err("username, email, and password are required")

    # Admin key validation
    if role == "ADMIN" and admin_key != ADMIN_REGISTRATION_KEY:
        return _err("Invalid admin registration key")

    # Duplicate checks
    dup_u = supabase.table("users").select("id").eq("username", username).execute()
    if dup_u.data:
        return _err("Username already taken")

    dup_e = supabase.table("users").select("id").eq("email", email).execute()
    if dup_e.data:
        return _err("Email already taken")

    # Insert user
    new_user = supabase.table("users").insert({
        "username": username,
        "email": email,
        "password": password,
        "role": role,
    }).execute().data[0]

    # Create matching profile
    profile = supabase.table("profiles").insert({
        "user_id": new_user["id"],
        "username": new_user["username"],
    }).execute().data[0]

    return jsonify(_user_with_profile(new_user, profile)), 201


# ---------------------------------------------------------------------------
# ── USERS ─────────────────────────────────────────────────────────────────
# ---------------------------------------------------------------------------

@app.route("/api/users", methods=["GET"])
def get_all_users():
    users = supabase.table("users").select("*").execute().data or []
    result = []
    for u in users:
        profile = _get_profile_for_user(u["id"])
        result.append(_user_with_profile(u, profile))
    return jsonify(result), 200


@app.route("/api/users/<user_id>", methods=["GET"])
def get_user(user_id):
    user, err = _one_or_404("users", user_id)
    if err:
        return err
    profile = _get_profile_for_user(user_id)
    return jsonify(_user_with_profile(user, profile)), 200


@app.route("/api/users/<user_id>", methods=["PUT"])
def update_user(user_id):
    user, err = _one_or_404("users", user_id)
    if err:
        return err

    body = request.get_json(force=True) or {}
    updates = {}

    if body.get("username"):
        new_username = body["username"].strip()
        if new_username != user["username"]:
            dup = supabase.table("users").select("id").eq("username", new_username).execute()
            if dup.data:
                return _err("Username already taken")
        updates["username"] = new_username

    if body.get("email"):
        new_email = body["email"].strip()
        if new_email != user["email"]:
            dup = supabase.table("users").select("id").eq("email", new_email).execute()
            if dup.data:
                return _err("Email already taken")
        updates["email"] = new_email

    if body.get("password"):
        updates["password"] = body["password"]

    if body.get("role"):
        updates["role"] = body["role"]

    if not updates:
        profile = _get_profile_for_user(user_id)
        return jsonify(_user_with_profile(user, profile)), 200

    updated = supabase.table("users").update(updates).eq("id", user_id).execute().data[0]
    profile = _get_profile_for_user(user_id)
    return jsonify(_user_with_profile(updated, profile)), 200


@app.route("/api/users/<user_id>", methods=["DELETE"])
def delete_user(user_id):
    user, err = _one_or_404("users", user_id)
    if err:
        return err
    supabase.table("users").delete().eq("id", user_id).execute()
    return "", 204


# ---------------------------------------------------------------------------
# ── PROFILES ──────────────────────────────────────────────────────────────
# ---------------------------------------------------------------------------

@app.route("/api/profiles/<user_id>", methods=["GET"])
def get_profile(user_id):
    profile = _get_profile_for_user(user_id)
    if not profile:
        return _err(f"Profile not found for user: {user_id}", 404)
    return jsonify(_profile_response(profile)), 200


@app.route("/api/profiles", methods=["POST"])
def create_profile():
    body = request.get_json(force=True) or {}
    user_id = body.get("userId")
    if not user_id:
        return _err("userId is required")

    user, err = _one_or_404("users", user_id)
    if err:
        return err

    existing = _get_profile_for_user(user_id)
    if existing:
        return _err(f"Profile already exists for user: {user_id}")

    insert_data = {
        "user_id": user_id,
        "username": user["username"],
        "bio": body.get("bio"),
        "avatar_url": body.get("avatarUrl"),
        "location": body.get("location"),
        "date_of_birth": body.get("dateOfBirth"),
        "website": body.get("website"),
        "accent_color": body.get("accentColor"),
        "avatar_seed": body.get("avatarSeed"),
    }
    profile = supabase.table("profiles").insert(insert_data).execute().data[0]
    return jsonify(_profile_response(profile)), 201


@app.route("/api/profiles/<user_id>", methods=["PUT"])
def update_profile(user_id):
    profile = _get_profile_for_user(user_id)
    if not profile:
        return _err(f"Profile not found for user: {user_id}", 404)

    body = request.get_json(force=True) or {}
    updates = {}
    field_map = {
        "bio": "bio",
        "avatarUrl": "avatar_url",
        "location": "location",
        "dateOfBirth": "date_of_birth",
        "website": "website",
        "accentColor": "accent_color",
        "avatarSeed": "avatar_seed",
    }
    for json_key, db_key in field_map.items():
        if body.get(json_key) is not None:
            updates[db_key] = body[json_key]

    if not updates:
        return jsonify(_profile_response(profile)), 200

    updated = supabase.table("profiles").update(updates).eq("id", profile["id"]).execute().data[0]
    return jsonify(_profile_response(updated)), 200


@app.route("/api/profiles/<user_id>", methods=["DELETE"])
def delete_profile(user_id):
    profile = _get_profile_for_user(user_id)
    if not profile:
        return _err(f"Profile not found for user: {user_id}", 404)
    supabase.table("profiles").delete().eq("id", profile["id"]).execute()
    return "", 204


# ---------------------------------------------------------------------------
# ── GAMES ─────────────────────────────────────────────────────────────────
# ---------------------------------------------------------------------------

@app.route("/api/games", methods=["GET"])
def get_all_games():
    type_filter = request.args.get("type", "").strip()
    q = supabase.table("games").select("*")
    if type_filter:
        q = q.eq("type", type_filter)
    games = q.execute().data or []
    return jsonify([_game_response(g) for g in games]), 200


@app.route("/api/games/simulators", methods=["GET"])
def get_simulator_games():
    games = supabase.table("games").select("*").eq("type", "simulator").execute().data or []
    return jsonify([_game_response(g) for g in games]), 200


@app.route("/api/games/active", methods=["GET"])
def get_active_games():
    type_filter = request.args.get("type", "").strip()
    q = supabase.table("games").select("*").eq("active", True)
    if type_filter:
        q = q.eq("type", type_filter)
    games = q.execute().data or []
    return jsonify([_game_response(g) for g in games]), 200


@app.route("/api/games/<game_id>", methods=["GET"])
def get_game(game_id):
    game, err = _one_or_404("games", game_id)
    if err:
        return err
    return jsonify(_game_response(game)), 200


@app.route("/api/games/<game_id>/code", methods=["GET"])
def get_game_code(game_id):
    """Return plain text game code, not JSON"""
    game, err = _one_or_404("games", game_id)
    if err:
        return err
    return (game.get("game_code") or ""), 200, {'Content-Type': 'text/plain'}


@app.route("/api/games", methods=["POST"])
def create_game():
    body = request.get_json(force=True) or {}
    insert_data = {
        "title": body.get("title"),
        "description": body.get("description"),
        "category": body.get("category"),
        "thumbnail": body.get("thumbnail"),
        "active": body.get("active", True),
        "game_code": body.get("gameCode"),
        "is_dynamic": body.get("isDynamic", False),
        "type": body.get("type") or "game",
    }
    game = supabase.table("games").insert(insert_data).execute().data[0]
    return jsonify(_game_response(game)), 201


@app.route("/api/games/<game_id>", methods=["PUT"])
def update_game(game_id):
    game, err = _one_or_404("games", game_id)
    if err:
        return err

    body = request.get_json(force=True) or {}
    updates = {}
    field_map = {
        "title": "title",
        "description": "description",
        "category": "category",
        "thumbnail": "thumbnail",
        "active": "active",
        "gameCode": "game_code",
        "isDynamic": "is_dynamic",
        "type": "type",
    }
    for json_key, db_key in field_map.items():
        if body.get(json_key) is not None:
            updates[db_key] = body[json_key]

    if not updates:
        return jsonify(_game_response(game)), 200

    updated = supabase.table("games").update(updates).eq("id", game_id).execute().data[0]
    return jsonify(_game_response(updated)), 200


@app.route("/api/games/<game_id>", methods=["DELETE"])
def delete_game(game_id):
    supabase.table("games").delete().eq("id", game_id).execute()
    return "", 204


# ---------------------------------------------------------------------------
# ── GAME SESSIONS ─────────────────────────────────────────────────────────
# ---------------------------------------------------------------------------

def _resolve_game_or_simulator(game_id: str) -> dict | None:
    """Return a game row, creating one from a simulator if needed."""
    res = supabase.table("games").select("*").eq("id", game_id).maybe_single().execute()
    if res.data:
        return res.data

    sim_res = supabase.table("simulators").select("*").eq("id", game_id).maybe_single().execute()
    if not sim_res.data:
        return None
    sim = sim_res.data
    # Create a matching game row from the simulator
    new_game = supabase.table("games").insert({
        "id": sim["id"],
        "title": sim.get("title"),
        "description": sim.get("description"),
        "category": sim.get("category"),
        "thumbnail": sim.get("thumbnail"),
        "active": sim.get("active", True),
        "type": "simulator",
        "game_code": sim.get("simulator_code"),
        "is_dynamic": sim.get("is_dynamic", True),
    }).execute().data[0]
    return new_game


@app.route("/api/sessions/start", methods=["POST"])
def start_session():
    user_id = request.args.get("userId", "").strip()
    game_id = request.args.get("gameId", "").strip()
    if not user_id or not game_id:
        return _err("userId and gameId query params are required")

    user, err = _one_or_404("users", user_id)
    if err:
        return err

    game = _resolve_game_or_simulator(game_id)
    if not game:
        return _err("Game or Simulator not found", 404)

    session_id = str(uuid.uuid4())
    result = supabase.table("game_sessions").insert({
        "id": session_id,
        "user_id": user_id,
        "game_id": game["id"],
        "start_time": _now_iso(),
    }).execute()
    
    if result.data is None or len(result.data) == 0:
        return _err("Failed to create session", 500)
    
    session = result.data[0]
    return jsonify(_session_response(session)), 201


@app.route("/api/sessions/end/<session_id>", methods=["PUT"])
def end_session(session_id):
    session, err = _one_or_404("game_sessions", session_id)
    if err:
        return err

    if session.get("end_time"):
        return _err("Session already ended")

    end_time = datetime.now(timezone.utc)
    start_time = datetime.fromisoformat(session["start_time"])
    if start_time.tzinfo is None:
        start_time = start_time.replace(tzinfo=timezone.utc)
    duration_secs = int((end_time - start_time).total_seconds())

    result = supabase.table("game_sessions").update({
        "end_time": end_time.isoformat(),
        "duration": duration_secs,
    }).eq("id", session_id).execute()
    
    if result.data is None or len(result.data) == 0:
        return _err("Failed to update session", 500)
    
    updated = result.data[0]
    return jsonify(_session_response(updated)), 200


@app.route("/api/sessions/user/<user_id>", methods=["GET"])
def get_sessions_by_user(user_id):
    sessions = (
        supabase.table("game_sessions")
        .select("*")
        .eq("user_id", user_id)
        .order("start_time", desc=True)
        .execute()
        .data or []
    )
    return jsonify([_session_response(s) for s in sessions]), 200


@app.route("/api/sessions/game/<game_id>", methods=["GET"])
def get_sessions_by_game(game_id):
    sessions = (
        supabase.table("game_sessions")
        .select("*")
        .eq("game_id", game_id)
        .order("start_time", desc=True)
        .execute()
        .data or []
    )
    return jsonify([_session_response(s) for s in sessions]), 200


@app.route("/api/sessions/active/user/<user_id>", methods=["GET"])
def get_active_sessions(user_id):
    sessions = (
        supabase.table("game_sessions")
        .select("*")
        .eq("user_id", user_id)
        .is_("end_time", "null")
        .execute()
        .data or []
    )
    return jsonify([_session_response(s) for s in sessions]), 200


@app.route("/api/sessions/total-time/user/<user_id>", methods=["GET"])
def get_total_play_time(user_id):
    sessions = (
        supabase.table("game_sessions")
        .select("duration")
        .eq("user_id", user_id)
        .not_.is_("end_time", "null")
        .execute()
        .data or []
    )
    total = sum(s.get("duration") or 0 for s in sessions)
    return jsonify(total), 200


@app.route("/api/sessions/dau", methods=["GET"])
def get_daily_active_users():
    date_str = request.args.get("date", "").strip()
    if not date_str:
        return _err("date query param is required")

    try:
        dt = datetime.fromisoformat(date_str)
    except ValueError:
        return _err("Invalid date format. Use ISO 8601 (e.g. 2026-07-18T00:00:00)")

    if dt.tzinfo is None:
        dt = dt.replace(tzinfo=timezone.utc)
    start = dt.replace(hour=0, minute=0, second=0, microsecond=0)
    end = start.replace(hour=23, minute=59, second=59, microsecond=999999)

    sessions = (
        supabase.table("game_sessions")
        .select("user_id")
        .gte("start_time", start.isoformat())
        .lte("start_time", end.isoformat())
        .execute()
        .data or []
    )
    distinct_users = len(set(s["user_id"] for s in sessions))
    return jsonify(distinct_users), 200


@app.route("/api/sessions/count/user/<user_id>", methods=["GET"])
def get_session_count(user_id):
    sessions = (
        supabase.table("game_sessions")
        .select("id")
        .eq("user_id", user_id)
        .execute()
        .data or []
    )
    return jsonify(len(sessions)), 200


# ---------------------------------------------------------------------------
# ── SCORES ────────────────────────────────────────────────────────────────
# ---------------------------------------------------------------------------

@app.route("/api/scores", methods=["POST"])
def submit_score():
    """Submit a score using the PostgreSQL upsert_score function."""
    body = request.get_json(force=True) or {}
    user_id = body.get("userId")
    game_id = body.get("gameId")
    score_value = body.get("scoreValue")

    if not user_id or not game_id or score_value is None:
        return _err("userId, gameId, and scoreValue are required", 400)

    # Verify user exists
    user, err = _one_or_404("users", user_id)
    if err:
        return err

    # Verify game exists
    game = _resolve_game_or_simulator(game_id)
    if not game:
        return _err("Game or Simulator not found", 404)

    try:
        # 🔥 NEW: Use the PostgreSQL function
        result = supabase.rpc('upsert_score', {
            'p_user_id': user_id,
            'p_game_id': game["id"],
            'p_score_value': score_value
        }).execute()
        
        # Check if we got data back
        if not result or not hasattr(result, 'data') or not result.data:
            return _err("Failed to save score - no data returned from function", 500)
        
        # The function returns a row, convert to dict
        saved = result.data[0] if isinstance(result.data, list) else result.data
        
        return jsonify(_score_response(saved)), 201
        
    except Exception as e:
        print(f"Error in submit_score: {str(e)}")
        import traceback
        print(traceback.format_exc())
        return _err(f"Database error: {str(e)}", 500)


@app.route("/api/scores/game/<game_id>", methods=["GET"])
def get_scores_by_game(game_id):
    scores = (
        supabase.table("scores")
        .select("*")
        .eq("game_id", game_id)
        .order("score_value", desc=True)
        .execute()
        .data or []
    )
    return jsonify([_score_response(s) for s in scores]), 200


@app.route("/api/scores/user/<user_id>", methods=["GET"])
def get_scores_by_user(user_id):
    scores = (
        supabase.table("scores")
        .select("*")
        .eq("user_id", user_id)
        .order("score_value", desc=True)
        .execute()
        .data or []
    )
    return jsonify([_score_response(s) for s in scores]), 200


@app.route("/api/scores/leaderboard/<game_id>", methods=["GET"])
def get_leaderboard(game_id):
    scores = (
        supabase.table("scores")
        .select("*")
        .eq("game_id", game_id)
        .order("score_value", desc=True)
        .limit(10)
        .execute()
        .data or []
    )
    return jsonify([_score_response(s) for s in scores]), 200


@app.route("/api/scores/global-leaderboard", methods=["GET"])
def get_global_leaderboard():
    scores = supabase.table("scores").select("user_id, score_value").execute().data or []
    totals: dict[str, int] = {}
    for s in scores:
        uid = s["user_id"]
        totals[uid] = totals.get(uid, 0) + (s.get("score_value") or 0)

    if not totals:
        return jsonify([]), 200

    users = supabase.table("users").select("id, username").in_("id", list(totals.keys())).execute().data or []
    username_map = {u["id"]: u["username"] for u in users}

    result = sorted(
        [
            {"userId": uid, "username": username_map.get(uid, ""), "totalScore": total}
            for uid, total in totals.items()
        ],
        key=lambda x: x["totalScore"],
        reverse=True,
    )
    return jsonify(result), 200


@app.route("/api/scores/personal-best", methods=["GET"])
def get_personal_best():
    user_id = request.args.get("userId", "").strip()
    game_id = request.args.get("gameId", "").strip()
    if not user_id or not game_id:
        return _err("userId and gameId query params are required")

    scores = (
        supabase.table("scores")
        .select("*")
        .eq("user_id", user_id)
        .eq("game_id", game_id)
        .order("score_value", desc=True)
        .limit(1)
        .execute()
        .data or []
    )
    if not scores:
        return "", 204
    return jsonify(_score_response(scores[0])), 200


# ---------------------------------------------------------------------------
# ── ACHIEVEMENTS ──────────────────────────────────────────────────────────
# ---------------------------------------------------------------------------

@app.route("/api/achievements", methods=["GET"])
def get_all_achievements():
    achievements = supabase.table("achievements").select("*").execute().data or []
    return jsonify([_achievement_response(a) for a in achievements]), 200


@app.route("/api/achievements/<achievement_id>", methods=["GET"])
def get_achievement(achievement_id):
    achievement, err = _one_or_404("achievements", achievement_id)
    if err:
        return err
    return jsonify(_achievement_response(achievement)), 200


@app.route("/api/achievements", methods=["POST"])
def create_achievement():
    body = request.get_json(force=True) or {}
    insert_data = {
        "title": body.get("title"),
        "description": body.get("description"),
        "required_score": body.get("requiredScore", 0),
    }
    result = supabase.table("achievements").insert(insert_data).execute()
    if result.data is None or len(result.data) == 0:
        return _err("Failed to create achievement", 500)
    achievement = result.data[0]
    return jsonify(_achievement_response(achievement)), 201


@app.route("/api/achievements/<achievement_id>", methods=["PUT"])
def update_achievement(achievement_id):
    achievement, err = _one_or_404("achievements", achievement_id)
    if err:
        return err

    body = request.get_json(force=True) or {}
    updates = {}
    if body.get("title") is not None:
        updates["title"] = body["title"]
    if body.get("description") is not None:
        updates["description"] = body["description"]
    if body.get("requiredScore") is not None and body["requiredScore"] > 0:
        updates["required_score"] = body["requiredScore"]

    if not updates:
        return jsonify(_achievement_response(achievement)), 200

    result = supabase.table("achievements").update(updates).eq("id", achievement_id).execute()
    if result.data is None or len(result.data) == 0:
        return _err("Failed to update achievement", 500)
    
    updated = result.data[0]
    return jsonify(_achievement_response(updated)), 200


@app.route("/api/achievements/<achievement_id>", methods=["DELETE"])
def delete_achievement(achievement_id):
    supabase.table("achievements").delete().eq("id", achievement_id).execute()
    return "", 204


# ---------------------------------------------------------------------------
# ── USER ACHIEVEMENTS ─────────────────────────────────────────────────────
# ---------------------------------------------------------------------------

def _unlock_achievement_internal(user_id: str, achievement_id: str) -> dict:
    """Unlock an achievement (inner logic, raises on error)."""
    dup = (
        supabase.table("user_achievements")
        .select("id")
        .eq("user_id", user_id)
        .eq("achievement_id", achievement_id)
        .execute()
        .data
    )
    if dup:
        raise RuntimeError("Achievement already unlocked for this user")

    user, err = _one_or_404("users", user_id)
    if err:
        raise RuntimeError("User not found")

    achievement, err = _one_or_404("achievements", achievement_id)
    if err:
        raise RuntimeError("Achievement not found")

    ua_id = str(uuid.uuid4())
    result = supabase.table("user_achievements").insert({
        "id": ua_id,
        "user_id": user_id,
        "achievement_id": achievement_id,
        "achievement_title": achievement.get("title"),
        "achievement_description": achievement.get("description"),
        "unlocked_at": _now_iso(),
    }).execute()
    
    if result.data is None or len(result.data) == 0:
        raise RuntimeError("Failed to unlock achievement")
    
    return result.data[0]


@app.route("/api/user-achievements/unlock", methods=["POST"])
def unlock_achievement():
    user_id = request.args.get("userId", "").strip()
    achievement_id = request.args.get("achievementId", "").strip()
    if not user_id or not achievement_id:
        return _err("userId and achievementId query params are required")

    try:
        ua = _unlock_achievement_internal(user_id, achievement_id)
    except RuntimeError as e:
        return _err(str(e))

    return jsonify(_user_achievement_response(ua)), 201


@app.route("/api/user-achievements/user/<user_id>", methods=["GET"])
def get_achievements_for_user(user_id):
    uas = supabase.table("user_achievements").select("*").eq("user_id", user_id).execute().data or []
    return jsonify([_user_achievement_response(ua) for ua in uas]), 200


@app.route("/api/user-achievements/achievement/<achievement_id>", methods=["GET"])
def get_users_for_achievement(achievement_id):
    uas = (
        supabase.table("user_achievements")
        .select("*")
        .eq("achievement_id", achievement_id)
        .execute()
        .data or []
    )
    return jsonify([_user_achievement_response(ua) for ua in uas]), 200


@app.route("/api/user-achievements/count/user/<user_id>", methods=["GET"])
def get_achievement_count(user_id):
    uas = supabase.table("user_achievements").select("id").eq("user_id", user_id).execute().data or []
    return jsonify(len(uas)), 200


@app.route("/api/user-achievements/check", methods=["POST"])
def check_and_unlock():
    user_id = request.args.get("userId", "").strip()
    score_str = request.args.get("score", "").strip()
    if not user_id or not score_str:
        return _err("userId and score query params are required")

    try:
        score = int(score_str)
    except ValueError:
        return _err("score must be an integer")

    all_achievements = supabase.table("achievements").select("*").execute().data or []
    already_unlocked_ids = {
        ua["achievement_id"]
        for ua in (
            supabase.table("user_achievements")
            .select("achievement_id")
            .eq("user_id", user_id)
            .execute()
            .data or []
        )
    }

    newly_unlocked = []
    for a in all_achievements:
        if (a.get("required_score") or 0) <= score and a["id"] not in already_unlocked_ids:
            try:
                ua = _unlock_achievement_internal(user_id, a["id"])
                newly_unlocked.append(_user_achievement_response(ua))
            except RuntimeError:
                pass

    return jsonify(newly_unlocked), 200


# ---------------------------------------------------------------------------
# ── NOTIFICATIONS ─────────────────────────────────────────────────────────
# ---------------------------------------------------------------------------

@app.route("/api/notifications/active", methods=["GET"])
def get_active_notifications():
    now = _now_iso()
    notifications = (
        supabase.table("notifications")
        .select("*")
        .eq("active", True)
        .order("created_at", desc=True)
        .execute()
        .data or []
    )
    result = [
        n for n in notifications
        if n.get("expires_at") is None or n["expires_at"] > now
    ]
    return jsonify([_notification_response(n) for n in result]), 200


@app.route("/api/notifications", methods=["GET"])
def get_all_notifications():
    notifications = (
        supabase.table("notifications")
        .select("*")
        .order("created_at", desc=True)
        .execute()
        .data or []
    )
    return jsonify([_notification_response(n) for n in notifications]), 200


@app.route("/api/notifications", methods=["POST"])
def create_notification():
    admin_id = request.args.get("adminId", "").strip()
    if not admin_id:
        return _err("adminId query param is required")

    admin, err = _one_or_404("users", admin_id)
    if err:
        return err

    if admin.get("role") != "ADMIN":
        return _err("Only admins can create notifications")

    body = request.get_json(force=True) or {}
    
    notification_id = str(uuid.uuid4())
    result = supabase.table("notifications").insert({
        "id": notification_id,
        "title": body.get("title"),
        "message": body.get("message"),
        "type": body.get("type"),
        "created_by_id": admin_id,
        "created_by_username": admin["username"],
        "expires_at": body.get("expiresAt"),
        "active": True,
    }).execute()
    
    if result.data is None or len(result.data) == 0:
        return _err("Failed to create notification", 500)
    
    notification = result.data[0]
    return jsonify(_notification_response(notification)), 200


@app.route("/api/notifications/<notification_id>", methods=["PUT"])
def update_notification(notification_id):
    notification, err = _one_or_404("notifications", notification_id)
    if err:
        return err

    body = request.get_json(force=True) or {}
    updates = {
        "title": body.get("title"),
        "message": body.get("message"),
        "type": body.get("type"),
        "expires_at": body.get("expiresAt"),
    }
    updates = {k: v for k, v in updates.items() if v is not None}

    result = supabase.table("notifications").update(updates).eq("id", notification_id).execute()
    if result.data is None or len(result.data) == 0:
        return _err("Failed to update notification", 500)
    
    updated = result.data[0]
    return jsonify(_notification_response(updated)), 200


@app.route("/api/notifications/<notification_id>", methods=["DELETE"])
def delete_notification(notification_id):
    supabase.table("notifications").delete().eq("id", notification_id).execute()
    return "", 204


@app.route("/api/notifications/<notification_id>/toggle", methods=["PATCH"])
def toggle_notification(notification_id):
    notification, err = _one_or_404("notifications", notification_id)
    if err:
        return err

    new_active = not notification.get("active", False)
    result = supabase.table("notifications").update({"active": new_active}).eq("id", notification_id).execute()
    
    if result.data is None or len(result.data) == 0:
        return _err("Failed to toggle notification", 500)
    
    updated = result.data[0]
    return jsonify(_notification_response(updated)), 200


# ---------------------------------------------------------------------------
# ── SIMULATORS ────────────────────────────────────────────────────────────
# ---------------------------------------------------------------------------

@app.route("/api/simulators", methods=["GET"])
def get_all_simulators():
    simulators = supabase.table("simulators").select("*").execute().data or []
    return jsonify([_simulator_response(s) for s in simulators]), 200


@app.route("/api/simulators/active", methods=["GET"])
def get_active_simulators():
    simulators = supabase.table("simulators").select("*").eq("active", True).execute().data or []
    return jsonify([_simulator_response(s) for s in simulators]), 200


@app.route("/api/simulators/<simulator_id>", methods=["GET"])
def get_simulator(simulator_id):
    sim, err = _one_or_404("simulators", simulator_id)
    if err:
        return err
    return jsonify(_simulator_response(sim)), 200


@app.route("/api/simulators/<simulator_id>/code", methods=["GET"])
def get_simulator_code(simulator_id):
    """Return plain text simulator code, not JSON"""
    sim, err = _one_or_404("simulators", simulator_id)
    if err:
        return err
    return (sim.get("simulator_code") or ""), 200, {'Content-Type': 'text/plain'}


@app.route("/api/simulators", methods=["POST"])
def create_simulator():
    body = request.get_json(force=True) or {}
    insert_data = {
        "title": body.get("title"),
        "description": body.get("description"),
        "category": body.get("category"),
        "thumbnail": body.get("thumbnail"),
        "active": body.get("active", True),
        "simulator_code": body.get("simulatorCode"),
        "is_dynamic": body.get("isDynamic", True),
        "type": body.get("type") or "simulator",
    }
    
    result = supabase.table("simulators").insert(insert_data).execute()
    if result.data is None or len(result.data) == 0:
        return _err("Failed to create simulator", 500)
    
    sim = result.data[0]
    return jsonify(_simulator_response(sim)), 201


@app.route("/api/simulators/<simulator_id>", methods=["PUT"])
def update_simulator(simulator_id):
    sim, err = _one_or_404("simulators", simulator_id)
    if err:
        return err

    body = request.get_json(force=True) or {}
    updates = {}
    field_map = {
        "title": "title",
        "description": "description",
        "category": "category",
        "thumbnail": "thumbnail",
        "active": "active",
        "simulatorCode": "simulator_code",
        "isDynamic": "is_dynamic",
        "type": "type",
    }
    for json_key, db_key in field_map.items():
        if body.get(json_key) is not None:
            updates[db_key] = body[json_key]

    if not updates:
        return jsonify(_simulator_response(sim)), 200

    result = supabase.table("simulators").update(updates).eq("id", simulator_id).execute()
    if result.data is None or len(result.data) == 0:
        return _err("Failed to update simulator", 500)
    
    updated = result.data[0]
    return jsonify(_simulator_response(updated)), 200


@app.route("/api/simulators/<simulator_id>", methods=["DELETE"])
def delete_simulator(simulator_id):
    supabase.table("simulators").delete().eq("id", simulator_id).execute()
    return "", 204


# ---------------------------------------------------------------------------
# Global error handler
# ---------------------------------------------------------------------------

@app.route("/", methods=["GET"])
def root():
    return jsonify({
        "timestamp": _now_iso(),
        "status": 200,
        "message": "GameZone API is running. Use /auth/login, /auth/register, or /api/games for available endpoints.",
    }), 200


@app.errorhandler(404)
def handle_not_found(e):
    return jsonify({
        "timestamp": _now_iso(),
        "status": 404,
        "error": "Not Found",
        "message": "The requested URL was not found on the server. Use / for API info or a valid endpoint such as /auth/login.",
    }), 404


@app.errorhandler(Exception)
def handle_exception(e):
    return jsonify({
        "timestamp": _now_iso(),
        "status": 400,
        "error": "Bad Request",
        "message": str(e),
    }), 400


# ---------------------------------------------------------------------------
# Firebase Functions v2 entry point
# ---------------------------------------------------------------------------

@https_fn.on_request(
    region="us-central1",
    cors=CorsOptions(
        cors_origins="*",
        cors_methods=["GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"],
    ),
)
def gamezone(req: https_fn.Request) -> https_fn.Response:
    """Single Firebase Function that routes all requests through Flask."""
    with app.request_context(req.environ):
        return app.full_dispatch_request()