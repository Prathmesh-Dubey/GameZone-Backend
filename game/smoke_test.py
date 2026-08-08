import importlib.util
import pathlib
import sys

ROOT = pathlib.Path('.').resolve()
sys.path.insert(0, str(ROOT))
spec = importlib.util.spec_from_file_location('game_main', ROOT / 'main.py')
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)
client = module.app.test_client()
for path in ['/', '/does-not-exist']:
    response = client.get(path)
    print(path, response.status_code, response.get_json())
