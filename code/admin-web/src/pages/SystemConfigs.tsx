import { Card, Empty } from 'antd';

const SystemConfigs = () => {
  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>系统配置</h2>
      <Card>
        <Empty description="系统配置骨架已就位，待接入真实 Config API" />
      </Card>
    </div>
  );
};

export default SystemConfigs;
