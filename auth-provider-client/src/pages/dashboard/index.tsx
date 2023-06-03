import DashboardPageLayout, {
  MenuItem,
} from "@/components/DashboardPageLayout";
import { ContentBox } from "codedepo-themed-components-nextjs";
import Head from "next/head";

export default function MyDashboardPage() {
  return (
    <>
      <Head>
        <link rel="icon" type="image/svg" sizes="32x32" href="/favicon.svg" />
      </Head>
      <DashboardPageLayout currentPage={MenuItem.Profile}>
        <ContentBox className="w-full h-52"></ContentBox>
      </DashboardPageLayout>
    </>
  );
}
