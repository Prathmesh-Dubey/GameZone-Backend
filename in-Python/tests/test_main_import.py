import importlib.util
import os
import pathlib
import sys

ROOT = pathlib.Path(__file__).resolve().parents[1]

for key in ("SUPABASE_URL", "SUPABASE_SERVICE_ROLE_KEY"):
    os.environ.pop(key, None)

sys.path.insert(0, str(ROOT))

spec = importlib.util.spec_from_file_location("game_main", ROOT / "main.py")
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)

assert hasattr(module, "app")


def test_root_and_missing_paths_return_helpful_json():
    client = module.app.test_client()

    root_response = client.get("/")
    assert root_response.status_code == 200
    root_payload = root_response.get_json()
    assert root_payload["status"] == 200
    assert "available" in root_payload["message"].lower()

    missing_response = client.get("/does-not-exist")
    assert missing_response.status_code == 404
    missing_payload = missing_response.get_json()
    assert missing_payload["status"] == 404
    assert "not found" in missing_payload["message"].lower()
